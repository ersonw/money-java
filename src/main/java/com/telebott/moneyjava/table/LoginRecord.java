package com.telebott.moneyjava.table;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Getter
@Setter
@Document(collection = "loginRecord")
public class LoginRecord implements Serializable {
    public LoginRecord(){}
    public LoginRecord(String userId, String ip){
        this.userId = userId;
        this.ip = ip;
    }
    public LoginRecord(String userId, String ip, boolean fail){
        this.userId = userId;
        this.ip = ip;
        this.fail = fail;
    }
    @Id
    @GeneratedValue
    private String id;
    private String userId;
    private String ip;
    private boolean fail=false;
    private Long addTime = System.currentTimeMillis();
}
