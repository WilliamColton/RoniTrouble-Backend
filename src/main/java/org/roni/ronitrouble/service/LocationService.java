package org.roni.ronitrouble.service;

import com.junmoyu.ip2region.RegionSearcher;
import org.roni.ronitrouble.component.cache.impl.IPCache;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final IPCache iPCache;

    public LocationService(IPCache iPCache) {
        this.iPCache = iPCache;
    }

    public String getLocationByUserId(Integer userId) {
        String ip = iPCache.getIP(userId);
        if (ip == null || ip.isBlank()) {
            return "";
        }
        return RegionSearcher.getRegion(ip).getProvince();
    }

}
