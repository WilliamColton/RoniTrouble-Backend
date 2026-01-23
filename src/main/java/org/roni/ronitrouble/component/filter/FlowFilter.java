package org.roni.ronitrouble.component.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FlowFilter extends OncePerRequestFilter {

    private final static long loginUserTokenValidity = 60L;
    private final static long windowSize = 60L;
    private final static long maxFlowRate = 50L;

    private final static RedisScript<Boolean> flowLuaScript = flowLuaScriptInit();
    private final RedisTemplate<String, String> redisTemplate;

    private static RedisScript<Boolean> flowLuaScriptInit() {
        DefaultRedisScript<Boolean> flowLuaScript = new DefaultRedisScript<>();
        flowLuaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/lua/flow.lua")));
        flowLuaScript.setResultType(Boolean.class);
        return flowLuaScript;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long currentTimeStamp = System.currentTimeMillis();
        long timestampBeforeATimeWindow = currentTimeStamp - windowSize * 1000;
        String userId = request.getUserPrincipal() == null ? "" : request.getUserPrincipal().getName();
        if (isAllowed(userId, currentTimeStamp, timestampBeforeATimeWindow)) {
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException("系统当前流量过大，请稍后再试");
        }
    }

    private Boolean isAllowed(String userId, long currentTimeStamp, long timestampBeforeATimeWindow) {
        return redisTemplate.execute(flowLuaScript, List.of(), userId, String.valueOf(FlowFilter.maxFlowRate), String.valueOf(currentTimeStamp), String.valueOf(timestampBeforeATimeWindow), String.valueOf(FlowFilter.loginUserTokenValidity));
    }

    @Scheduled(cron = "0 * * * * ?")
    public void cleanExpiredDataOfUserTraffic() {
        long timestampBeforeATimeWindow = System.currentTimeMillis() - windowSize * 1000;
        redisTemplate.boundZSetOps("userTraffic").removeRangeByScore(-1, timestampBeforeATimeWindow);
    }


}
