package com.telebott.moneyjava.table;

import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.util.ToolsUtil;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Getter
@Document(collection = "adminUser")
public class AdminUser implements Serializable {
    @Id
    @GeneratedValue
    private String id;
    private String username = null;
    private String password = null;
    private String salt = ToolsUtil.getSalt();
    private boolean enabled = false;



    private Long addTime = System.currentTimeMillis();
    private Long updateTime = System.currentTimeMillis();
    @Transient
    private String token;
    public AdminUser setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public static AdminUser format(String user) {
        if (user != null) {
            return JSONObject.toJavaObject(JSONObject.parseObject(user), AdminUser.class);
        }
        return null;
    }
    public AdminUser setId(String id) {
        this.id = id;
        return this;
    }

    public AdminUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public AdminUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public AdminUser setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public AdminUser setAddTime(Long addTime) {
        this.addTime = addTime;
        return this;
    }

    public AdminUser setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public AdminUser setToken(String token) {
        this.token = token;
        return this;
    }
}
