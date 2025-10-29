package com.main.ioteacher.user.service;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationRateLimiter {

    private final Map<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    public boolean allowRequest(String ip) {
        LocalDateTime now = LocalDateTime.now();
        RequestInfo info = requestMap.get(ip);

        if (info == null) {
            requestMap.put(ip, new RequestInfo(1, now));
            return true;
        }

        if (now.isBefore(info.windowStart.plusMinutes(1))) {
            if (info.count >= 3) return false;
            info.count++;
        } else {
            info.count = 1;
            info.windowStart = now;
        }

        requestMap.put(ip, info);
        return true;
    }

    private static class RequestInfo {
        int count;
        LocalDateTime windowStart;
        RequestInfo(int count, LocalDateTime windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }
}
