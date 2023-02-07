package com.telebott.moneyjava.table;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@Getter
@Document(collection = "applyDetails")
public class ApplyDetails {
    @Id
    @GeneratedValue
    private String id;
    private String userId;
    private String orderNo;
    private String contractNo;
    private double money;
    private double monthly;
    private long installments;
    private double interest;
    private Long addTime=System.currentTimeMillis();
    private Long updateTime=System.currentTimeMillis();
   public ApplyDetails setId(String id) {
        this.id = id;
        return this;
    }

    public ApplyDetails setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ApplyDetails setOrderNo(String orderNo) {
        this.orderNo = orderNo;
       return this;
   }

    public ApplyDetails setContractNo(String contractNo) {
        this.contractNo = contractNo;
        return this;
    }

    public ApplyDetails setMoney(double money) {
        this.money = money;
       return this;
   }

   public ApplyDetails setMonthly(double monthly) {
        this.monthly = monthly;
       return this;
   }
   public ApplyDetails setInstallments(long installments) {
        this.installments = installments;
       return this;
   }

   public ApplyDetails setInterest(double interest) {
        this.interest = interest;
       return this;
   }

   public ApplyDetails setAddTime(Long addTime) {
        this.addTime = addTime;
       return this;
   }

   public ApplyDetails setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
       return this;
   }
}
