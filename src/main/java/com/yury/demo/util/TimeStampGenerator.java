package com.yury.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeStampGenerator {
    @Value("${application.timezone}")
    private String timezone;

    @Value("${application.tspattern}")
    private String timestampPattern = "yyyy-MM-dd HH:mm:ss.SSS";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);

    private TimeStampGenerator() { }

    public String getCurrentTimeStamp(){
        return ZonedDateTime.now(ZoneId.of(timezone)).format(formatter);
    }
}
