package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public class AdminUserDao extends MongoAnimal<AdminUser> {
    public AdminUserDao() {
        super(AdminUser.class);
    }
    public AdminUser findByUsername(String username) {
        return super.findBy(
                super.getMatch(
                        super.where("username").is(username)
                )
        );
    }
}
