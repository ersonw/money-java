package com.telebott.moneyjava.table;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Getter
@Document(collection = "smsConfig")
public class SmsConfig implements Serializable {
    @Id
    @GeneratedValue
    private String id;
    private String user;
    private String password;
    private String name;
    private long smsCountMaxDay = 0;
    private long send = 0;
    private long last = 0;
    private String balance;
    private boolean enabled = false;
    private long addTime = System.currentTimeMillis();
    private long updateTime = System.currentTimeMillis();

    public SmsConfig setId(String id) {
        this.id = id;
        return this;
    }

    public SmsConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public SmsConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public SmsConfig setName(String name) {
        this.name = name;
        return this;
    }

    public SmsConfig setSmsCountMaxDay(long smsCountMaxDay) {
        this.smsCountMaxDay = smsCountMaxDay;
        return this;
    }

    public SmsConfig setSend(long send) {
        this.send = send;
        return this;
    }

    public SmsConfig setLast(long last) {
        this.last = last;
        return this;
    }

    public SmsConfig setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public SmsConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SmsConfig setAddTime(long addTime) {
        this.addTime = addTime;
        return this;
    }

    public SmsConfig setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
