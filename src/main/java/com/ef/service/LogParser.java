package com.ef.service;

import com.ef.Constants;
import com.ef.model.Log;
import com.ef.utils.LoggingUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ef.Constants.*;

@Component(value = "logParser")
public class LogParser {

    private SimpleDateFormat logSimpleDateFormat = new SimpleDateFormat(ACCESS_LOG_DATE_FORMAT);

    private Logger logger = Logger.getLogger(LogParser.class);

    @Autowired
    private LogManager logManager;

    public void parser(String filePath, String startDate, String duration, String thresholdAsString){
        try {
            File file = new File(filePath);
            Date start = new SimpleDateFormat(PARAMETER_DATE_FORMAT).parse(startDate);
            Date end;
            if(Constants.DAILY.equals(duration)){
                end=new Instant(start.getTime()).plus(org.joda.time.Duration.standardDays(1)).toDate();
            }
            else{
                end=new Instant(start.getTime()).plus(org.joda.time.Duration.standardHours(1)).toDate();
            }
            LoggingUtils.infoIfEnabled(logger, "Parameters: Start: "+start+", duration:"+duration+
                    ", threshold:"+thresholdAsString+", file:"+filePath);
            LoggingUtils.debugIfEnabled(logger,"Start:"+startDate+", end:"+end);

            List<String> lines = FileUtils.readLines(file, "UTF-8");
            int threshold = Integer.parseInt(thresholdAsString);
            Set<String> result = doParse(threshold, lines, start, end);
            LoggingUtils.infoIfEnabled(logger, "Parse ended. Result: "+result);
            LoggingUtils.infoIfEnabled(logger, "Persisting result: "+result.size()+" ips");
            logManager.saveResult(result, start, end, threshold);
            LoggingUtils.infoIfEnabled(logger, "Persisting result done.");
        } catch (IOException e) {
            logger.error("File not found");
        } catch (ParseException e) {
            logger.error("startDate format must be: yyyy-MM-dd HH:mm:ss.SSS. Threshold must be an integer.");
        }
    }

    @VisibleForTesting
    Set<String> doParse(int threshold, List<String> lines, Date start, Date end) throws ParseException {
        Set<String> resultIps = Sets.newHashSet();
        Map<String, Integer> ipCount = Maps.newHashMap();
        List<Log> logs = Lists.newArrayList();

        LoggingUtils.infoIfEnabled(logger, "Start parsing "+lines.size()+" lines.");
        for(String line : lines){
            Log log = parseLine(line);
            logs.add(log);
            if (log.getStartDate().after(start) && log.getStartDate().before(end)) {
                ipCount.put(log.getIp(), ipCount.getOrDefault(log.getIp(),0)+1);
                if (ipCount.get(log.getIp()) > threshold) {
                    resultIps.add(log.getIp());
                }
            }
        }
        LoggingUtils.infoIfEnabled(logger, "Persisting "+logs.size()+" logs");
        logManager.save(logs);
        LoggingUtils.infoIfEnabled(logger, "Persisting logs done.");
        return resultIps;

    }


    private Log parseLine(String line) throws ParseException {
        String values[] = line.split(SEPARATOR);
        Log log = new Log();
        log.setStartDate(logSimpleDateFormat.parse(values[0]));
        log.setIp(values[1]);
        log.setRequest(values[2]);
        log.setStatus(Integer.parseInt(values[3]));
        log.setUserAgent(values[4]);
        return log;
    }
}
