package com.main.ioteacher.external.repository;

import com.main.ioteacher.external.entity.ExternalProgramApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalProgramApplicationRepository extends JpaRepository<ExternalProgramApplication, Long> {
}
