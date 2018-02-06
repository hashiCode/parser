package com.ef.service;

import com.ef.TestRoot;
import com.ef.model.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LogManagerTest extends TestRoot{

    @Autowired
    private LogManager logManager;

    @Test
    public void testPersistLog(){
        Log log = new Log();
        log.setIp("192.168.106.134");
        log.setRequest("GET / HTTP/1.1");
        log.setStartDate(new Date());
        log.setStatus(200);
        log.setUserAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; EIE10;ENUSWOL)");

        logManager.save(Lists.newArrayList(log));

        Query query = this.getEntityManager().createQuery("from Log");
        List resultList = query.getResultList();
        assertEquals(1, resultList.size());
        Log logSaved = (Log) resultList.get(0);
        assertEquals(log, logSaved);

    }

    @Test
    public void testResult(){
        logManager.saveResult(Sets.newHashSet("192.168.106.134"),  new Date(), new Date(), 200);
        Query query = this.getEntityManager().createQuery("from Result");
        List resultList = query.getResultList();
        assertEquals(1, resultList.size());

    }
}
