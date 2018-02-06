package com.ef;

import com.ef.model.Result;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest extends TestRoot{

    private File accessLog, emptyAccessLog;

    @Before
    public void setup(){
        ClassLoader classLoader = getClass().getClassLoader();
        accessLog = new File(classLoader.getResource("access.log").getFile());
        emptyAccessLog = new File(classLoader.getResource("empty.log").getFile());
        Parser.setSpringConfigFile("spring-config-test.xml");
        Parser.setCloseContext(false);
    }

    @Test
    public void testReturnEmptyResult$EmptyFile() {
        Parser.main(new String[]{"--accesslog="+emptyAccessLog.getPath(),"--startDate=2017-01-01.15:00:00", "--duration=hourly", "--threshold=100"});
        Query query = this.getEntityManager().createQuery("from Log");
        List resultList = query.getResultList();
        assertEquals(0, resultList.size());

        query = this.getEntityManager().createQuery("from Result");
        resultList = query.getResultList();
        assertEquals(0, resultList.size());

    }

    @Test
    public void testReturnEmptyResult$DateOutOfRange() throws IOException {
        Parser.main(new String[]{"--accesslog="+accessLog.getPath(),"--startDate=2018-01-01.15:00:00", "--duration=hourly", "--threshold=100"});
        Query query = this.getEntityManager().createQuery("from Log");
        List resultList = query.getResultList();
        assertEquals(FileUtils.readLines(accessLog).size(), resultList.size());

        query = this.getEntityManager().createQuery("from Result");
        resultList = query.getResultList();
        assertEquals(0, resultList.size());
    }

    @Test
    public void testReturnEmptyResult$ThreshouldNotReached() throws IOException {
        Parser.main(new String[]{"--accesslog="+accessLog.getPath(),"--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=100"});

        Query query = this.getEntityManager().createQuery("from Log");
        List resultList = query.getResultList();
        assertEquals(FileUtils.readLines(accessLog).size(), resultList.size());

        query = this.getEntityManager().createQuery("from Result");
        resultList = query.getResultList();
        assertEquals(0, resultList.size());

    }

    @Test
    public void testValidResult() throws IOException{
        Parser.main(new String[]{"--accesslog="+accessLog.getPath(),"--startDate=2017-01-01.15:00:00", "--duration=hourly", "--threshold=1"});

        Query query = this.getEntityManager().createQuery("from Log");
        List resultList = query.getResultList();
        assertEquals(FileUtils.readLines(accessLog).size(), resultList.size());

        query = this.getEntityManager().createQuery("from Result");
        resultList = query.getResultList();
        assertEquals(2, resultList.size());

        List<String> ipsResult = Lists.newArrayList();

        for(Object r : resultList){
            Result result = (Result) r;
            ipsResult.add(result.getIp());
        }

        assertTrue(ipsResult.contains("192.168.209.39"));
        assertTrue(ipsResult.contains("192.168.51.54"));

    }
}
