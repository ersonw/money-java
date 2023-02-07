package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.SmsConfig;
import org.springframework.stereotype.Repository;

@Repository
public class SmsConfigDao extends MongoAnimal<SmsConfig> {
    public SmsConfigDao() {
        super(SmsConfig.class);
    }
}
