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
public class SmsRecord implements Serializable {
    @Id
    @GeneratedValue
    private String id;
    private String code;
    private String phone;
    private String data;
    private String ip;
    private int status;
    private long addTime;
    private long updateTime;
}
