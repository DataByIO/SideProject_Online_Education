package com.main.ioteacher.common.repository;

import com.main.ioteacher.common.entity.MailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailLogRepository extends JpaRepository<MailLog, Long> {
}
