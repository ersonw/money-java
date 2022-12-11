package com.telebott.moneyjava.service;

import com.telebott.moneyjava.config.BusinessException;
import com.telebott.moneyjava.dao.LoginRecordDao;
import com.telebott.moneyjava.dao.SmsRecordDao;
import com.telebott.moneyjava.dao.UserDao;
import com.telebott.moneyjava.data.RequestHeader;
import com.telebott.moneyjava.data.ResponseData;
import com.telebott.moneyjava.data.SmsCode;
import com.telebott.moneyjava.redis.AuthRedis;
import com.telebott.moneyjava.table.LoginRecord;
import com.telebott.moneyjava.table.SmsRecord;
import com.telebott.moneyjava.table.User;
import com.telebott.moneyjava.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class ApiService {
    private static final int MAX_FAIL_COUNT = 5;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthRedis authRedis;
    @Autowired
    private LoginRecordDao loginRecordDao;
    @Autowired
    private SmsRecordDao smsRecordDao;

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
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setIp(header.getIp());
        smsRecord.setCode(smsCode.getCode());
        smsRecord.setPhone(smsCode.getPhone());
        smsRecord.setStatus(0);
        smsRecord.setAddTime(System.currentTimeMillis());
        smsRecordDao.save(smsRecord);
        if (SmsBaoUtil.sendSmsCode(smsCode)){
            smsRecord.setStatus(1);
            smsRecordDao.save(smsRecord);
            return ResponseData.success(ResponseData.object("id", smsCode.getId()));
        }
        authRedis.popCode(smsCode);
        return ResponseData.fail("短信发送失败，请联系管理员!");
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
        return ResponseData.success("登录成功！",ResponseData.object("token",user.getToken()));
    }
}
