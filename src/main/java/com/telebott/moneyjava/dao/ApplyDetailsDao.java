package com.telebott.moneyjava.dao;

import com.telebott.moneyjava.config.MongoAnimal;
import com.telebott.moneyjava.table.ApplyDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplyDetailsDao extends MongoAnimal<ApplyDetails> {
    public ApplyDetailsDao() {
        super(ApplyDetails.class);
    }
    public ApplyDetails getByOrderNo(String orderNo,String userId){
        return super.findBy(super.getMatch(
                super.and(
                        super.where("orderNo").is(orderNo),
                        super.where("userId").is(userId)
                )
        ));
    }
    public ApplyDetails getByUserId(String userId){
        return super.findBy(super.getMatch(super.where("userId").is(userId)));
    }
    public long countAllByUserId(String userId) {
        return super.count(
                super.getMatch(
                        super.where("userId").is(userId)
                )
        );
    }
    public Page<ApplyDetails> findAllByUserId(String userId, Pageable pageable){
        AggregationOperation mach = super.getMatch(
                super.where("userId").is(userId)
        );
        List<ApplyDetails> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
}
