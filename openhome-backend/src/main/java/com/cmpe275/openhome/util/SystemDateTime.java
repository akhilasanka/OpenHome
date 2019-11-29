package com.cmpe275.openhome.util;

import java.time.LocalDateTime;

public class SystemDateTime {
    public static LocalDateTime getCurSystemTime() {
        return LocalDateTime.now();
    }
}
