package com.telebott.moneyjava.table;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Getter
@Setter
@Document(collection = "smsRecord")
public class SmsConfig implements Serializable {
    @Id
    @GeneratedValue
    private String id;
    private String user;
    private String password;
    private String name;
    private long smsCountMaxDay;
    private String balance;
    private boolean enabled;
    private long addTime;
    private long updateTime;
}
