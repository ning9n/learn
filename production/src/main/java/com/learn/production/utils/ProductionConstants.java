package com.learn.production.utils;

import java.util.concurrent.TimeUnit;

public class ProductionConstants {
    public static final String VIDEO_ID_KEY_PREFIX="video:id:";
    public static final String VIDEO_PARSE_KEY ="video:parse:";
    public static final Integer VIDEO_PARSE_TTL=5;
    public static final TimeUnit VIDEO_PARSE_TIMEUNIT=TimeUnit.HOURS;
}
