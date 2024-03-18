package com.eu.habbo.util;

import ch.qos.logback.core.pattern.color.ANSIConstants;

public class ANSI {

    private ANSI(){}

    private static final String HEADER = "\u001B[";

    public static final String RED = HEADER + ANSIConstants.RED_FG + "m";
    public static final String GREEN = HEADER + ANSIConstants.GREEN_FG + "m";
    public static final String YELLOW = HEADER + ANSIConstants.YELLOW_FG + "m";
    public static final String BLUE = HEADER + ANSIConstants.BLUE_FG + "m";
    public static final String MAGENTA = HEADER + ANSIConstants.MAGENTA_FG + "m";
    public static final String CYAN = HEADER + ANSIConstants.CYAN_FG + "m";
    public static final String WHITE = HEADER + ANSIConstants.WHITE_FG + "m";
    public static final String DEFAULT = HEADER + ANSIConstants.DEFAULT_FG + "m";

}
