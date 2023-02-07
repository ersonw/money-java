package com.telebott.moneyjava.service;

import com.telebott.moneyjava.config.BusinessException;
import com.telebott.moneyjava.dao.AdminLoginRecordDao;
import com.telebott.moneyjava.dao.AdminUserDao;
import com.telebott.moneyjava.data.ObjectData;
import com.telebott.moneyjava.data.RequestHeader;
import com.telebott.moneyjava.data.ResponseData;
import com.telebott.moneyjava.redis.AuthRedis;
import com.telebott.moneyjava.table.AdminLoginRecord;
import com.telebott.moneyjava.table.AdminUser;
import com.telebott.moneyjava.util.MD5Util;
import com.telebott.moneyjava.util.TimeUtil;
import com.telebott.moneyjava.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private AuthRedis authRedis;
    @Autowired
    private AdminUserDao adminUserDao;
    @Autowired
    private AdminLoginRecordDao adminLoginRecordDao;
    private static final int MAX_FAIL_COUNT = 5;

    public long getUserLoginFail(String userId) throws BusinessException {
        long today = TimeUtil.getTodayZero();
        long lastTime = adminLoginRecordDao.getLastLoginTime(userId);
        long time = Math.max(lastTime, today);
        long count = adminLoginRecordDao.countAllByUserId(userId, time);
        if (MAX_FAIL_COUNT <= count) throw new BusinessException("今日已超过密码重试次数，请明日再试!");
        return MAX_FAIL_COUNT - count;
    }
    public ResponseData login(String username, String password, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        AdminUser user = adminUserDao.findByUsername(username);
        if (user==null) throw new BusinessException("用户不存在！");
        if (!user.isEnabled()) throw new BusinessException("用户状态异常，请联系管理员！");
        long count = getUserLoginFail(user.getId());
        MD5Util md5Util = new MD5Util(user.getSalt());
        if (!user.getPassword().equals(md5Util.getPassWord(password))){
            adminLoginRecordDao.save(new AdminLoginRecord(user.getId(),header.getIp(),true));
            throw new BusinessException("密码错误，剩余"+(count-1)+"次机会!");
        }
        user.setToken(ToolsUtil.getToken());
        authRedis.put(user);
        adminLoginRecordDao.save(new AdminLoginRecord(user.getId(),header.getIp()));
        return ResponseData.success(ObjectData.object("token",user.getToken()).getObject());
    }
}
