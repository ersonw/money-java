package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.SmsConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SmsBaoConfigDao extends MongoAnimal<SmsConfig> {
    public SmsBaoConfigDao() {
        super(SmsConfig.class);
    }
    public SmsConfig getSmsConfig(){
        return super.findBy(
                super.getMatch(
                        super.where("enabled").is(true)
                )
        );
    }
}
