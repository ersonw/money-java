package com.telebott.moneyjava.redis;

import com.telebott.moneyjava.dao.UserDao;
import com.telebott.moneyjava.data.SmsCode;
import com.telebott.moneyjava.table.AdminUser;
import com.telebott.moneyjava.table.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Component
public class AuthRedis {
    @Resource
    private RedisTemplate<String, User> userRedisTemplate;
    @Resource
    private RedisTemplate<String, SmsCode> codeRedisTemplate;
    @Resource
    private RedisTemplate<String, AdminUser> adminUserRedisTemplate;
    @Autowired
    private UserDao userDao;
    public void put(AdminUser user){
//        deleteByAdminUserId(user.getId());
        adminUserRedisTemplate.opsForValue().set(user.getToken(),user);
    }
    public void deleteByAdminToken(String token){
        adminUserRedisTemplate.delete(token);
    }
    public AdminUser findByAdminToken(String token) {
        return adminUserRedisTemplate.opsForValue().get(token);
    }
    private List<AdminUser> getAllAdminUsers() {
        Set<String> keys = adminUserRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return adminUserRedisTemplate.opsForValue().multiGet(keys);
    }
    public AdminUser findByAdminUserId(String userId) {
        List<AdminUser> users = getAllAdminUsers();
        for (AdminUser user: users) {
            if (user.getId().equals(userId)) return user;
        }
        return null;
    }
    public void deleteByAdminUserId(String userId){
        List<AdminUser> users = getAllAdminUsers();
        for (AdminUser user: users) {
            if (user.getId().equals(userId)) {
                deleteByAdminToken(user.getToken());
            }
        }
    }
    public void put(User user){
//        userRedisTemplate.opsForValue().set(user.getToken(),user, Duration.of(60*15,SECONDS));
        deleteByUserId(user.getId());
        userRedisTemplate.opsForValue().set(user.getToken(),user);
    }
    public void deleteByToken(String token){
        userRedisTemplate.delete(token);
    }
    public User findByToken(String token) {
        return userRedisTemplate.opsForValue().get(token);
    }
    private List<User> getAllUsers() {
        Set<String> keys = userRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return userRedisTemplate.opsForValue().multiGet(keys);
    }
    public User findByUserId(String userId) {
        List<User> users = getAllUsers();
        for (User user: users) {
            if (user.getId().equals(userId)) return user;
        }
        return null;
    }
    public void deleteByUserId(String userId){
        User profile = userDao.findById(userId);
        if (profile == null) return;
        List<User> users = getAllUsers();
        for (User user: users) {
            if (user.getId().equals(userId)) {
                deleteByToken(user.getToken());
            }
        }
    }
    public void updateByUserId(String userId){
        User profile = userDao.findById(userId);
        if (profile == null) return;
        List<User> users = getAllUsers();
        for (User user: users) {
            if (user.getId().equals(userId)) {
                profile.setToken(user.getToken());
                put(profile);
            }
        }
    }
    public void pushCode(SmsCode smsCode){
        codeRedisTemplate.opsForValue().set(smsCode.getId(),smsCode, Duration.of(60*60,SECONDS));
    }
    public SmsCode findCode(String id){
        return codeRedisTemplate.opsForValue().get(id);
    }
    public void removeByPhone(String phone){
        SmsCode code = findByPhone(phone);
        while (code != null){
            codeRedisTemplate.delete(code.getId());
            code = findByPhone(phone);
        }
    }
    public SmsCode findByPhone(String phone){
        List<SmsCode> codes = getAllCodes();
        for (SmsCode code: codes) {
            if (code.getPhone().equals(phone)) return code;
        }
        return null;
    }
    private List<SmsCode> getAllCodes() {
        Set<String> keys = codeRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return codeRedisTemplate.opsForValue().multiGet(keys);
    }
    public void popCode(SmsCode code){
        codeRedisTemplate.delete(code.getId());
    }
}
