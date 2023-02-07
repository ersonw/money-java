package com.telebott.moneyjava.util;
import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.dao.SmsBaoConfigDao;
import com.telebott.moneyjava.dao.SmsRecordDao;
import com.telebott.moneyjava.data.SmsCode;
import com.telebott.moneyjava.table.SmsConfig;
import com.telebott.moneyjava.table.SmsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

@Component
public class SmsBaoUtil {
    private static SmsBaoUtil self;
    @Autowired
    public SmsBaoConfigDao smsBaoConfigDao;
    @Autowired
    public SmsRecordDao smsRecordDao;
    private static SmsConfig smsConfig;
    private static String user;
    private static String passwd;
    private static String name;
    public static Long smsCountMaxDay=0L;
    private static final String opt = "opt";
    private static final String opt_wsms = "wsms";
    private static final String opt_sms = "sms";
    private static final String opt_query = "query";
    private static final String httpUrl = "http://api.smsbao.com/opt";
    private static final String code_text = "【{name}】您的验证码是{code},60分钟内有效。若非本人操作请忽略此消息。";
    private static long lastQueryTime = 0L;
    public static boolean sendSmsCode(SmsCode smsCode){
        return sendSmsCode(smsCode.getPhone(),smsCode.getCode());
    }
    public static boolean sendSmsCode(String phone,String code){
        if ((System.currentTimeMillis()- 1000 * 60 * 1) > lastQueryTime){
            if (smsConfig == null) return false;
            lastQueryTime=System.currentTimeMillis();
            JSONObject object = _query();
            if (object != null){
                self.smsBaoConfigDao.save(smsConfig.setSend(object.getLong("send")).setLast(object.getLong("last")));
            }
//            System.out.println(object);
        }
//        handlerChangeMessage(phone, "653304", "test");
//        return true;
        String result = null;
        String str = code_text.replaceAll("\\{name\\}",name).replaceAll("\\{code\\}",code);
//        System.out.println(str);
//        return false;
        if (MobileRegularExp.isMobileNumber(phone) ){
            if (phone.contains("+")){
                if (phone.startsWith("+86")){
                    result =  _sendSms(phone.substring(3),str);
                }else {
                    result = _sendSms(phone,str,true);
                }
            }else if (phone.length() == 11){
                result = _sendSms(phone,str);
            }else {
                return false;
            }
        }
        if (result == null){
            return false;
        }
        String data = " 短信发送回执："+ echoCode(result);
        System.out.println(phone + data);
        handlerChangeMessage(phone,code,data);
        return  result.equals("0");
    }

    private static void handlerChangeMessage(String phone, String code, String data) {
        SmsRecord record = self.smsRecordDao.findByNumberCode(phone,code);
        if (record != null){
            record.setData(data);
            self.smsRecordDao.save(record);
        }
    }

    private static String _sendSms(String phone,String text){
        return _sendSms(phone, text, false);
    }
    private static String _sendSms(String phone,String text, boolean wsms){
        String url;
        if (wsms){
            url = httpUrl.replaceAll(opt,opt_wsms);
        }else {
            url = httpUrl.replaceAll(opt,opt_sms);
        }
        String httpArg = "u=" + user + "&p=" + md5(passwd) + "&m=" + encodeUrlString(phone, "UTF-8") + "&c=" + encodeUrlString(text, "UTF-8");
//        System.out.println(httpArg);
        return request(url, httpArg);
    }
    public static String echoCode(String result){
        int r = Integer.parseInt((result.split("\n")[0]));
        String res = "未知错误!";
        switch (r){
            case 0:
                res = "发送成功！";
                break;
            case 30:
                res = "密码错误！";
                break;
            case 40:
                res = "账号不存在！";
                break;
            case 41:
                res = "余额不足！";
                break;
            case 43:
                res = "IP地址限制！";
                break;
            case 50:
                res = "内容含有敏感词！";
                break;
            case 51:
                res = "手机号码不正确！";
                break;
            default:
                break;
        }
        return res;
    }
    public static JSONObject _query(){
        String data = "u="+user+"&p=" + md5(passwd);
        String result = request(httpUrl.replaceAll(opt,opt_query), data);
        String[] res = result.split("\n");
        JSONObject object = new JSONObject();
        if (res.length > 0 && res[0].equals("0")){
            String[] t = res[1].split(",");
            object.put("send",t[0]);
            object.put("last",t[1]);
            return object;
        }
        return null;
    }
    @PostConstruct
    public void init(){
        self = this;
        rest();
    }
    private void getSmsConfig(){
        SmsConfig config = self.smsBaoConfigDao.getSmsConfig();
        if (config != null){
            user = config.getUser();
            passwd = config.getPassword();
            name = config.getName();
            smsCountMaxDay = config.getSmsCountMaxDay();
            smsConfig = config;
        }
    }
    public static void rest(){
        self.getSmsConfig();
    }
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    public static String getSmsCode(){
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i=0;i< 6;i++){
            int num = random.nextInt(9);
            code.append(num);
        }
        return code.toString();
    }
    public static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }
}
