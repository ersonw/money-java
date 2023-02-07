package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.Details;
import org.springframework.stereotype.Repository;

@Repository
public class DetailsDao extends MongoAnimal<Details> {
    public DetailsDao() {
        super(Details.class);
    }
    public Details findByUserId(String userId) {
        return super.findBy(super.getMatch(super.where("userId").is(userId)));
    }
}
