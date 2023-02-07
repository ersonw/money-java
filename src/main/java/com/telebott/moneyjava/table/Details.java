package com.telebott.moneyjava.table;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@Getter
@Document(collection = "details")
public class Details {
    @Id
    @GeneratedValue
    private String id;
    private String userId;
    private String name;
    private String sfz;
    private String front;
    private String reverse;
    private String hand;
    private String phone;
    private String khh;
    private String yhk;
    private String sign;
    private int state=0;
    private Long addTime=System.currentTimeMillis();
    private Long updateTime=System.currentTimeMillis();

    public Details setId(String id) {
        this.id = id;
        return this;
    }

    public Details setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Details setName(String name) {
        this.name = name;
        return this;
    }

    public Details setSfz(String sfz) {
        this.sfz = sfz;
        return this;
    }

    public Details setFront(String front) {
        this.front = front;
        return this;
    }

    public Details setReverse(String reverse) {
        this.reverse = reverse;
        return this;
    }

    public Details setHand(String hand) {
        this.hand = hand;
        return this;
    }
    public Details setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Details setKhh(String khh) {
        this.khh = khh;
        return this;
    }

    public Details setYhk(String yhk) {
        this.yhk = yhk;
        return this;
    }
    public Details setSign(String sign) {
        this.sign = sign;
        return this;
    }
    public Details setState(int state) {
        this.state = state;
        return this;
    }

    public Details setAddTime(Long addTime) {
        this.addTime = addTime;
        return this;
    }

    public Details setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
