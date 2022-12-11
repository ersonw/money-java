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
@Document(collection = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue
    private String id;
    private String superior = null;
    private String phone = null;
    private String username = null;
    private String avatar = null;
    private String password = null;
    private String salt = ToolsUtil.getSalt();
    private String remark = null;
    private boolean enabled = false;
    private Long addTime = System.currentTimeMillis();
    private Long updateTime = System.currentTimeMillis();
    @Transient
    private String token;
//    @Transient
//    private static final long serialVersionUID = -6743567631108323096L;

    public User() {
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public User setSuperior(String superior) {
        this.superior = superior;
        return this;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }
    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }
    public User setRemark(String remark) {
        this.remark = remark;
        return this;
    }
    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public User setAddTime(Long addTime) {
        this.addTime = addTime;
        return this;
    }

    public User setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public User setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
    public JSONObject object(){
        return JSONObject.parseObject(this.toString());
    }

    public static User format(String user) {
        if (user != null) {
            return JSONObject.toJavaObject(JSONObject.parseObject(user), User.class);
        }
        return null;
    }
}
