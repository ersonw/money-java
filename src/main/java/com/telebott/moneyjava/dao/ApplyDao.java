package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.Apply;
import org.springframework.stereotype.Repository;

@Repository
public class ApplyDao extends MongoAnimal<Apply> {
    public ApplyDao() {
        super(Apply.class);
    }
    public Apply findByEnable(){
        return super.findBy(
                super.getMatch(
                        super.where("enabled").is(true)
                )
        );
    }
}

