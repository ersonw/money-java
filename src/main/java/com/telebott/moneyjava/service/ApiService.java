package com.telebott.moneyjava.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.config.BusinessException;
import com.telebott.moneyjava.dao.*;
import com.telebott.moneyjava.data.*;
import com.telebott.moneyjava.redis.AuthRedis;
import com.telebott.moneyjava.table.*;
import com.telebott.moneyjava.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ApiService {
    private static final int MAX_FAIL_COUNT = 5;
    private static final int PAGE_LIMIT = 30;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthRedis authRedis;
    @Autowired
    private LoginRecordDao loginRecordDao;
    @Autowired
    private SmsRecordDao smsRecordDao;
    @Autowired
    private SmsConfigDao smsConfigDao;
    @Autowired
    private DetailsDao detailsDao;
    @Autowired
    private ApplyDao applyDao;
    @Autowired
    private ApplyDetailsDao applyDetailsDao;

    public ResponseData loginPhoneSms(String phone, HttpServletRequest request) {
        if (!MobileRegularExp.isMobileNumber(phone)) throw new BusinessException( "手机号不正确！");
        if (!checkSmsMax(phone)) throw new BusinessException( "今日短信发送已达上限！");
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        long last = checkSmsLast(phone);
        if (last > 0){
            return ResponseData.fail("操作过于频繁，请在"+last+"秒后重试！");
        }
        SmsCode smsCode = authRedis.findByPhone(phone);
        if (smsCode== null){
           smsCode = new SmsCode(phone);
           authRedis.removeByPhone(phone);
        }
        authRedis.pushCode(smsCode);
        JSONObject object = ObjectData.object("id", smsCode.getId()).getObject();
        object.put("code",smsCode.getCode());
        return ResponseData.success(object);
//        SmsRecord smsRecord = new SmsRecord();
//        smsRecord.setIp(header.getIp());
//        smsRecord.setCode(smsCode.getCode());
//        smsRecord.setPhone(smsCode.getPhone());
//        smsRecord.setStatus(0);
//        smsRecord.setAddTime(System.currentTimeMillis());
//        smsRecordDao.save(smsRecord);
//        if (SmsBaoUtil.sendSmsCode(smsCode)){
//            smsRecord.setStatus(1);
//            smsRecordDao.save(smsRecord);
//            return ResponseData.success(ResponseData.object("id", smsCode.getId()));
//        }
//        authRedis.popCode(smsCode);
//        return ResponseData.fail("短信发送失败，请联系管理员!");
    }
    public long checkSmsLast(String phone){
        SmsRecord record = smsRecordDao.getLast(phone);
        if (record == null){
            return 0;
        }
        long last = System.currentTimeMillis() - record.getAddTime();
        long ms = 1000 * 60 * 2;
        if (last > ms){
            return 0;
        }
        return (ms - last) / 1000;
    }
    public boolean checkSmsMax(String phone){
        long count = smsRecordDao.countTodayMax(TimeUtil.getTodayZero(),phone);
        long max = SmsBaoUtil.smsCountMaxDay;
        return count < max;
    }
    public long getUserLoginFail(String userId) throws BusinessException {
        long today = TimeUtil.getTodayZero();
        long lastTime = loginRecordDao.getLastLoginTime(userId);
        long time = Math.max(lastTime, today);
        long count = loginRecordDao.countAllByUserId(userId, time);
        if (MAX_FAIL_COUNT <= count) throw new BusinessException("今日已超过密码重试次数，请明日再试!");
        return MAX_FAIL_COUNT - count;
    }
    public ResponseData loginPhone(String username, String password, String codeId, HttpServletRequest request) {
        if (!MobileRegularExp.isMobileNumber(username)) throw new BusinessException( "手机号不正确！");
        User user = userDao.findByPhone(username);
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        SmsCode smsCode = authRedis.findCode(codeId);
        if (smsCode == null) throw new BusinessException( "验证码已过期!");
        if (!smsCode.getPhone().equals(username)) throw new BusinessException( "手机号码不匹配!");
        if (!smsCode.getCode().equals(password)) throw new BusinessException( "验证码不正确!");
        authRedis.popCode(smsCode);
        if (user == null){
            user = new User();
            user.setPhone(username).setEnabled(true);

        }else if (!user.isEnabled()){
            throw new BusinessException( "用户状态异常，请联系管理员!");
        }else{
//            long count = getUserLoginFail(user.getId());
//            MD5Util md5 = new MD5Util(user.getSalt());
//            if (!user.getPassword().equals(md5.getPassWord(password))) {
//                loginRecordDao.save(new LoginRecord(user.getId(),header.getIp(),true));
//                throw new BusinessException( "密码输入错误!剩余"+(count-1)+"次机会");
//            }
        }
        user.setUpdateTime(System.currentTimeMillis());
        userDao.save(user);
        user.setToken(ToolsUtil.getToken());
        authRedis.put(user);
        loginRecordDao.save(new LoginRecord(user.getId(),header.getIp()));
        return ResponseData.success("登录成功！",ObjectData.object("token",user.getToken()).getObject());
    }

    public String test() {
//        smsConfigDao.save(new SmsConfig().setUser("81325").setPassword("81325").setName("春潮视频").setEnabled(true));
//        applyDao.save(new Apply().setMax(300000).setMini(30000).setFee(180).setStepping(1000).setEnabled(true));
        return "ok";
    }

    public ResponseData userChangePassword(String passwordOld, String password, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (StringUtils.isEmpty(password)) throw new BusinessException("新密码不可为空!");
        MD5Util md5 = new MD5Util(user.getSalt());
        if (StringUtils.isNotEmpty(user.getPassword())){
            if (!user.getPassword().equals(md5.getPassWord(passwordOld))) throw new BusinessException( "旧密码输入错误!");
        }
        if (MD5Util.MD5("").equals(password)) throw new BusinessException("新密码不可为空!");
        user.setPassword(md5.getPassWord(password));
        userDao.save(user);
        authRedis.put(user);
        return ResponseData.success("修改成功！",ObjectData.object("state",true));
    }

    public ResponseData upload(MultipartFile file, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (file.isEmpty()) throw new BusinessException("上传失败，因为文件是空的.");
        try {
            String filename = ToolsUtil.md5PHP(file.getOriginalFilename()+System.currentTimeMillis());
//            String filename = file.getOriginalFilename();
            if (filename.contains("..")){
                filename = filename.replaceAll("\\.\\./","");
            }
            String pathname = ToolsUtil.getBaseRootPath()+"upload/"+Objects.requireNonNull(filename);
            BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(new File(pathname).toPath()));
            out.write(file.getBytes());
            out.flush();
            out.close();
            return ResponseData.success(ObjectData.object("url","/res/"+filename));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new BusinessException("上传失败!");
        }
    }

    public ResponseData batchUpload(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        MultipartHttpServletRequest params=((MultipartHttpServletRequest) request);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        String name=params.getParameter("name");
        System.out.println("name:"+name);
        String id=params.getParameter("id");
        System.out.println("id:"+id);
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    String filename = ToolsUtil.md5PHP(file.getOriginalFilename()+System.currentTimeMillis());
//                    String filename = file.getOriginalFilename();
                    if (filename.contains("..")){
                        filename = filename.replaceAll("\\.\\./","");
                    }
                    String pathname = ToolsUtil.getBaseRootPath()+"upload/"+Objects.requireNonNull(filename);
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(Files.newOutputStream(new File(pathname).toPath()));
                    stream.write(bytes);
                    stream.close();
                    ids.add("/res/"+filename);
                } catch (Exception e) {
                    stream = null;
                    System.out.println(e.getMessage());
                    throw new BusinessException("You failed to upload " + i);
                }
            } else {
                throw new BusinessException("You failed to upload " + i
                        + " because the file was empty.");
            }
        }
        return ResponseData.success(ids);
    }

    public ResponseData uploadDelete(String filePath, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        return ResponseData.success();
    }

    public ResponseData detailsSfz(String name, String sfz, String front, String reverse, String hand, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (
                StringUtils.isEmpty(name) ||
                StringUtils.isEmpty(sfz) ||
                StringUtils.isEmpty(front) ||
                StringUtils.isEmpty(reverse) ||
                StringUtils.isEmpty(hand)
        ) throw new BusinessException("为了避免影响您的信用信息，身份信息每项必须完整填写!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details == null){
            details = new Details().setUserId(user.getId());
        }else{
//            if (details.getState() > -1) throw new BusinessException("资料已审核通过或者正在审核中无法提交新的信息!");
            if (details.getState() > -1) return ResponseData.success("提交成功，进行下一步!",ObjectData.object("state",true));
        }
        details.setName(name).setSfz(sfz)
                .setFront(front).setReverse(reverse)
                .setHand(hand)
                .setUpdateTime(System.currentTimeMillis());
        detailsDao.save(details);
//        return ResponseData.success("提交成功！等待审核");
        return ResponseData.success("提交成功，进行下一步!",ObjectData.object("state",true));
    }

    public ResponseData detailsSfzGet(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) return ResponseData.success();
        JSONObject object = new JSONObject();
        object.put("name",details.getName());
        object.put("sfz",details.getSfz());
        object.put("front",details.getFront());
        object.put("reverse",details.getReverse());
        object.put("hand",details.getHand());
        object.put("state",details.getState()>-1);
        return ResponseData.success(object);
    }

    public ResponseData detailsYhkGet(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) return ResponseData.success();
        JSONObject object = new JSONObject();
        object.put("name",details.getName());
        object.put("phone",details.getPhone());
        object.put("khh",details.getKhh());
        object.put("yhk",details.getYhk());
        object.put("state",details.getState()>-1);
        return ResponseData.success(object);
    }

    public ResponseData detailsYhk(String phone, String khh, String yhk, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (
                StringUtils.isEmpty(phone) ||
                        StringUtils.isEmpty(khh) ||
                        StringUtils.isEmpty(yhk)
        ) throw new BusinessException("为了避免影响您的下款速度，银行卡信息每项必须完整填写!");
        if (!MobileRegularExp.isMobileNumber(phone)) throw new BusinessException("为了避免影响您的下款速度，请填写正确的手机号!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) throw new BusinessException("请先填写身份信息!");
        details.setPhone(phone).setKhh(khh).setYhk(yhk)
                .setUpdateTime(System.currentTimeMillis());
        detailsDao.save(details);
        return ResponseData.success("提交成功，进行下一步!",ObjectData.object("state",true));
    }

    public ResponseData detailsSignGet(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) return ResponseData.success();
        JSONObject object = new JSONObject();
        object.put("image",details.getSign());
        object.put("state",details.getState()>-1);
        return ResponseData.success(object);
    }

    public ResponseData detailsSign(String url, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (
                StringUtils.isEmpty(url)
        ) throw new BusinessException("为了避免影响您的下款速度，银行卡信息每项必须完整填写!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) throw new BusinessException("请先完善身份信息以及银行卡信息!");
        if (details.getState()>-1) throw new BusinessException("状态审核中，不支持修改哟!");
        details.setSign(url).setState(0)
                .setUpdateTime(System.currentTimeMillis());
        detailsDao.save(details);
        return ResponseData.success("提交成功，等待审核!",ObjectData.object("state",true));
    }
    private String getBankNumber(String yhk){
        return "**** *** *** *** " + yhk.substring(yhk.length() - 4);
    }
    public ResponseData applyGet(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) return ResponseData.success(ObjectData.object("state",false));
        Apply apply = applyDao.findByEnable();
        if (apply == null) throw new BusinessException("系统错误，请联系管理员!");
        JSONObject object = new JSONObject();
        object.put("state",details.getState()>-1);
        object.put("name",details.getName());
        object.put("khh",details.getKhh());
        object.put("yhk",getBankNumber(details.getYhk()));
        object.put("mini",apply.getMini());
        object.put("max",apply.getMax());
        object.put("fee",apply.getFee());
        object.put("stepping",apply.getStepping());
        return ResponseData.success(object);
    }

    public ResponseData apply(double money, long installments, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        Details details = detailsDao.findByUserId(user.getId());
        if (details== null) return ResponseData.success(ObjectData.object("state",false));
        Apply apply = applyDao.findByEnable();
        if (apply == null) throw new BusinessException("系统错误，请联系管理员!");
        if (money<apply.getMini()||money>apply.getMax()) throw new BusinessException("提交金额超过可选范围，请重新提交!");
        if (installments<3||installments>36) throw new BusinessException("分期还款月数超过可选范围，请重新提交!");
        applyDetailsDao.save(
                new ApplyDetails().setUserId(user.getId())
                        .setInstallments(installments)
                        .setInterest(apply.getFee()).setMoney(money)
                        .setMonthly(money/installments+(money*apply.getFee()*30))
                        .setOrderNo("J"+System.currentTimeMillis()/1000)
        );
        return ResponseData.success(ObjectData.object("state",true));
    }

    public ResponseData order(int page,HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        page--;
        if (page<0) page=0;
        Pageable pageable = PageRequest.of(page,PAGE_LIMIT, Sort.by(Sort.Direction.DESC,"addTime"));
        Page<ApplyDetails> detailsPage = applyDetailsDao.findAllByUserId(user.getId(), pageable);
        JSONArray array = new JSONArray();
        for (ApplyDetails details : detailsPage.getContent()) {
            JSONObject object = new JSONObject();
            object.put("orderNo",details.getOrderNo());
            object.put("fee",details.getInterest());
            object.put("installments",details.getInstallments());
            object.put("money",details.getMoney());
            object.put("addTime",details.getAddTime());
            array.add(object);
        }
        return ResponseData.success(array,detailsPage.getTotalPages());
    }

    public ResponseData myGet(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        ApplyDetails details = applyDetailsDao.getByUserId(user.getId());
        JSONObject object = new JSONObject();
        object.put("balance",0);
        object.put("money",0);
        if (details!= null){
            object.put("money",details.getMoney());
        }
        return ResponseData.success(object);
    }

    public ResponseData orderNo(String orderNo, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        User user = header.getUserInterface();
        if (user == null) throw new BusinessException(201, "用户未登录,请先登录!");
        if (StringUtils.isEmpty(orderNo)) throw new BusinessException("合同订单号不能为空!");
        ApplyDetails details = applyDetailsDao.getByOrderNo(orderNo, user.getId());
        if (details==null)  throw new BusinessException("合同不存在!");
        Apply apply = applyDao.findByEnable();
        if (apply==null) throw new BusinessException("系统错误，请联系管理员!");
        String contract = apply.getContract();
        if (StringUtils.isEmpty(details.getContractNo())){
            details.setContractNo("CO"+ToolsUtil.getRandomCode(16));
        }
        return ResponseData.success();
    }
}
