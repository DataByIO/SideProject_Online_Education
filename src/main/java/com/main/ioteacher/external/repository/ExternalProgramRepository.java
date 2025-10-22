package com.main.ioteacher.external.repository;

import com.main.ioteacher.external.entity.ExternalProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalProgramRepository extends JpaRepository<ExternalProgram, Long> {
}
