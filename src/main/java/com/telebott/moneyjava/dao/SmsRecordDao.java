package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.SmsRecord;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class SmsRecordDao extends MongoAnimal<SmsRecord> {
    public SmsRecordDao() {
        super(SmsRecord.class);
    }
    public Long countTodayMax(long cTime,String phone){
        return super.count(
                super.getMatch(
                        super.and(
                                super.where("phone").is(phone),
                                super.where("addTime").gte(cTime)
                        )
                ),
                super.getGroup()
        );
    }
    public SmsRecord findByNumberCode(String phone,String code){
        return super.findBy(
                super.getMatch(
                        super.and(
                                super.where("phone").is(phone),
                                super.where("code").is(code)
                        )
                )
        );
    }
    public SmsRecord getLast(String phone){
        return super.findBy(
                super.getMatch(
                        super.where("phone").is(phone)
                ),
                super.getSort(Sort.by(Sort.Direction.DESC,"addTime"))
        );
    }
}
