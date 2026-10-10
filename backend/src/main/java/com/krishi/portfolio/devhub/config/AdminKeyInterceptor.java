package com.krishi.portfolio.devhub.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminKeyInterceptor implements HandlerInterceptor {

    private final byte[] adminKey;

    public AdminKeyInterceptor(@Value("${app.admin-key:}") String key) {
        this.adminKey = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws IOException {
        String m = req.getMethod();
        if (m.equals("GET") || m.equals("HEAD") || m.equals("OPTIONS")) return true; // reads and CORS preflight

        String given = req.getHeader("X-Admin-Key");
        boolean ok = adminKey.length > 0 && given != null
                && MessageDigest.isEqual(adminKey, given.getBytes(StandardCharsets.UTF_8));
        if (ok) return true;

        res.setStatus(401);
        res.setContentType("application/json");
        res.getWriter().write("{\"status\":401,\"error\":\"Admin key required\"}");
        return false;
    }
}