package com.ef.service;

import com.ef.Constants;
import com.ef.TestRoot;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogParserTest extends TestRoot{

    @Autowired
    private LogParser logParser;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.ACCESS_LOG_DATE_FORMAT);

    @Test
    public void testReturnEmptyResult() throws ParseException {
        List<String> lines = Lists.newArrayList();

        //empty lines
        Set<String> result = logParser.doParse(100, lines, new Date(), new Date());
        assertTrue(result.isEmpty());

        lines.add("2017-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        lines.add("2017-01-01 15:15:33.933|192.168.252.104|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60\"");

        //out of date range
        result = logParser.doParse(100, lines, new Date(), new Date());
        assertTrue(result.isEmpty());

        //threshould not reached
        result = logParser.doParse(100, lines, simpleDateFormat.parse("2017-01-01 00:00:00.000"), simpleDateFormat.parse("2017-01-01 20:00:00.000"));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testValidResult() throws ParseException {
        List<String> lines = Lists.newArrayList();

        lines.add("2017-01-01 00:00:11.763|192.168.234.104|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        lines.add("2017-01-01 15:15:33.933|192.168.234.104|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60\"");
        lines.add("2017-01-01 00:00:11.763|192.168.234.105|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        lines.add("2017-01-01 15:15:33.933|192.168.234.105|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60\"");

        Set<String> result = logParser.doParse(1, lines, simpleDateFormat.parse("2017-01-01 00:00:00.000"), simpleDateFormat.parse("2017-01-01 20:00:00.000"));

        assertEquals(2, result.size());
        assertTrue(result.contains("192.168.234.104"));
        assertTrue(result.contains("192.168.234.105"));

    }
}
