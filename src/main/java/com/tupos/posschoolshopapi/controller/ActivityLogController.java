package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.ActivityLog;
import com.tupos.posschoolshopapi.repository.ActivityLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity-log")
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogController(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping
    public List<ActivityLog> getAll() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }
}
