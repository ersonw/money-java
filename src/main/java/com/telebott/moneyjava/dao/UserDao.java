package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.User;
import com.telebott.moneyjava.util.ToolsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends MongoAnimal<User> {
    public UserDao() {
        super(User.class);
    }
    public User findByPhone(String phone){
        return super.findBy(
                super.getMatch(
                        super.where("phone").is(phone)
                )
        );
    }
}
