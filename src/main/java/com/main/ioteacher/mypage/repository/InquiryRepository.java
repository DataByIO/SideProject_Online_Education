package com.main.ioteacher.mypage.repository;

import com.main.ioteacher.mypage.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUser_UserIdOrderByCreatedAtDesc(String userId);
}
