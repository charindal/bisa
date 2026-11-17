package com.bisa.sis.audit;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(UUID actorUserId, String action, String targetType, String targetId, String detail) {
        repository.save(new AuditLog(actorUserId, action, targetType, targetId, detail));
    }
}
