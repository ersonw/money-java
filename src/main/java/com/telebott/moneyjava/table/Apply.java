package com.telebott.moneyjava.table;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@Getter
@Document(collection = "apply")
public class Apply {
    @Id
    @GeneratedValue
    private String id;
    private long mini=0;
    private long max=0;
    private double fee=0;
    private long stepping=0;
    private boolean enabled = false;
    private String contract;
    private Long addTime=System.currentTimeMillis();
    private Long updateTime=System.currentTimeMillis();
    public Apply setId(String id) {
        this.id = id;
        return this;
    }

    public Apply setMini(long mini) {
        this.mini = mini;
        return this;
    }

    public Apply setMax(long max) {
        this.max = max;
        return this;
    }

    public Apply setFee(long fee) {
        this.fee = fee;
        return this;
    }

    public Apply setStepping(long stepping) {
        this.stepping = stepping;
        return this;
    }

    public Apply setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Apply setFee(double fee) {
        this.fee = fee;
        return this;
    }

    public Apply setContract(String contract) {
        this.contract = contract;
        return this;
    }

    public Apply setAddTime(Long addTime) {
        this.addTime = addTime;
        return this;
    }

    public Apply setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
