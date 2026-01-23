package org.roni.ronitrouble.component.cache.impl;

import com.junmoyu.ip2region.RegionSearcher;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.component.cache.StringCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IPCache extends StringCache {

    public String getIP(Integer userId) {
        return getValue(userId.toString());
    }

    public void setIP(String ip, Integer userId) {
        try {
            if (RegionSearcher.getRegion(ip) == null) {
                log.info("未通过ip获取到地址，暂不更新地址");
            }
        } catch (RuntimeException e) {
            log.info("未通过ip获取到地址，暂不更新地址");
            throw new RuntimeException(e);
        }
        setValue(userId.toString(), ip, 7, TimeUnit.DAYS);
    }

}
