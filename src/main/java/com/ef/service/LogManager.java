package com.ef.service;

import com.ef.model.Log;
import com.ef.model.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Repository
@Transactional
public class LogManager {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(List<Log> logs) {
        for(Log log : logs){
            this.entityManager.persist(log);
        }
    }

    public void saveResult(Set<String> resultIps, Date start, Date end, int threshold) {
        for(String ip : resultIps){
            Result result = new Result();
            result.setIp(ip);
            result.setComment("Made more than "+threshold+ " requests between "+start+" and "+end);
            entityManager.persist(result);
        }
    }
}
