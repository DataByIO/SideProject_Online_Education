package com.main.ioteacher.inquiry.repository;

import com.main.ioteacher.inquiry.entity.InquiryAnswer;
import com.main.ioteacher.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ✅ 답변 Repository
 * - 문의 ID 기준으로 답변 조회 가능
 */
@Repository
public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    /** ✅ 특정 문의에 대한 답변 1건 조회 */
    Optional<InquiryAnswer> findByInquiry(Inquiry inquiry);
}
