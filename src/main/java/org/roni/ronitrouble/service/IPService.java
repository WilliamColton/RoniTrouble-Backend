package org.roni.ronitrouble.service;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.IPCache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IPService {

    private final IPCache ipCache;

    public String getIP(Integer userId) {
        return ipCache.getIP(userId);
    }

    public void uploadIP(String ip, Integer userId) {
        ipCache.setIP(ip, userId);
    }

}
