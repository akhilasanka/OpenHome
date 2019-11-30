package com.cmpe275.openhome.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SystemDateTime {	
	public static long OFFSET = 0;
    public static LocalDateTime getCurSystemTime() {
        return LocalDateTime.now().plus(OFFSET, ChronoUnit.HOURS);
    }
    
    public static void addToOffset(long numHours) {
    	OFFSET += numHours;
    }
}
