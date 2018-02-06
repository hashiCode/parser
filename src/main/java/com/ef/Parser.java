package com.ef;

import com.ef.service.LogParser;
import com.ef.utils.LoggingUtils;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static com.ef.Constants.*;

public class Parser {

    private static Logger logger = Logger.getLogger(Parser.class);

    public static void main(String args[]) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
        LogParser logParser =(LogParser) applicationContext.getBean("logParser");
        LoggingUtils.infoIfEnabled(logger,"Initializing");

        Options options = new Options();
        Option optionAccessLogPath = new Option("", PARAMETER_ACCESSLOG, true, "Start date in format: yyyy-MM-dd.HH:mm:ss");
        optionAccessLogPath.setRequired(true);

        Option optionStartDate = new Option("", PARAMETER_STARTDATE, true, "Start date in format: yyyy-MM-dd.HH:mm:ss");
        optionStartDate.setRequired(true);

        Option optionDuration = new Option("", PARAMETER_DURATION, true, "Duration. Possibles values are: \"hourly\" and \"daily\"");
        optionDuration.setRequired(true);

        Option optionThreshold = new Option("", PARAMETER_THRESHOLD, true, "Threshold (number)");
        optionThreshold.setRequired(true);

        options.addOption(optionAccessLogPath);
        options.addOption(optionStartDate);
        options.addOption(optionDuration);
        options.addOption(optionThreshold);

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String accessLogPath = cmd.getOptionValue(PARAMETER_ACCESSLOG);
            String startDate = cmd.getOptionValue(PARAMETER_STARTDATE);
            String duration = cmd.getOptionValue(PARAMETER_DURATION);
            String threshold = cmd.getOptionValue(PARAMETER_THRESHOLD);

            logParser.parser(accessLogPath, startDate, duration, threshold);
            LoggingUtils.infoIfEnabled(logger, "Application ended.");
            ((ConfigurableApplicationContext) applicationContext).close();

        } catch (ParseException e) {
            logger.error("Error parsing parameters. " +
                    "accesslog must be a file path to an file, startDate must have \"yyyy-MM-dd.HH:mm:ss\" format," +
                    "duration must be \"daily\" or \"hourly\" and threshold must be a number");
        }

    }
}
