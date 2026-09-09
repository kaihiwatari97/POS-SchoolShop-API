package com.tupos.posschoolshopapi.service;

import com.tupos.posschoolshopapi.model.ActivityLog;
import com.tupos.posschoolshopapi.repository.ActivityLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void log(String action, Double amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "desconocido";

        ActivityLog entry = new ActivityLog();
        entry.setAction(action);
        entry.setAmount(amount);
        entry.setUsername(username);
        entry.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(entry);
    }
}
