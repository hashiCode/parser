package com.ef.utils;

import org.apache.log4j.Logger;

public class LoggingUtils {

    public static void debugIfEnabled(Logger logger, String message){
        if(logger.isDebugEnabled()){
            logger.debug(message);
        }
    }

    public static void infoIfEnabled(Logger logger, String message){
        if(logger.isInfoEnabled()){
            logger.info(message);
        }
    }
}
