package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.AdminLoginRecord;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminLoginRecordDao extends MongoAnimal<AdminLoginRecord> {
    public AdminLoginRecordDao() {
        super(AdminLoginRecord.class);
    }
    public long getLastLoginTime(String userId) {
        List<AdminLoginRecord> records = super.aggregate(
                super.getMatch(
                        super.and(
                                super.where("userId").is(userId),
                                super.where("fail").is(false)
                        )
                ),super.getSort(Sort.by(Sort.Direction.DESC,"addTime")),super.getLimit(1));
        if (records.size() == 0) return 0;
        return records.get(0).getAddTime();
    }
    public long countAllByUserId(String userId, long time) {
        return super.count(
                super.getMatch(
                        super.and(
                                super.where("userId",userId),
                                super.where("addTime").gte(time),
                                super.where("fail").is(true)
                        )
                ),
                super.getGroup()
        );
    }
}
