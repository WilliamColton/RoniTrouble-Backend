package org.roni.ronitrouble.component.cache.impl;

import org.roni.ronitrouble.component.cache.StringCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IPCache extends StringCache {

    public String getIP(Integer userId) {
        return getValue(userId.toString());
    }

    public void setIP(String ip, Integer userId) {
        setValue(userId.toString(), ip, 7, TimeUnit.DAYS);
    }

}
