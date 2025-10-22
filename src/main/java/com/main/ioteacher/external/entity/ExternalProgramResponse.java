package com.main.ioteacher.external.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ExternalProgramResponse {

    private Long programId;
    private String category;
    private String title;
    private String subtitle;
    private String recruitmentPeriod;
    private String recruitmentCapacity;
    private String status;
    private String recruitmentTarget;
    private LocalDate examDate;
    private String selectionCriteria;
    private String resultNotice;
    private String contact;
    private String mainImageUrl;
    private String educationPeriod;
    private String educationLocation;
    private String completionCriteria;
    private List<String> topics;
    private List<String> benefits;
    private List<Map<String, Object>> schedule;

    // ✅ 강사 관련 필드
    private String instructorName;
    private String instructorProfileImage;
    private String instructorBio; // ✅ String으로 변경
    private List<String> instructorBio2; // ✅ 배열로 유지

    public static ExternalProgramResponse from(ExternalProgram p, String lang) {
        return ExternalProgramResponse.builder()
                .programId(p.getProgramId())
                .category(p.getCategory())
                .mainImageUrl(p.getMainImageUrl())
                .title(extract(p.getTitle(), lang))
                .subtitle(extract(p.getSubtitle(), lang))
                .recruitmentPeriod(p.getRecruitmentPeriod())
                .recruitmentCapacity(p.getRecruitmentCapacity())
                .status(p.getStatus() != null ? p.getStatus().name().toLowerCase() : "upcoming")
                .recruitmentTarget(extract(p.getRecruitmentTarget(), lang))
                .examDate(p.getExamDate())
                .selectionCriteria(extract(p.getSelectionCriteria(), lang))
                .resultNotice(p.getResultNotice())
                .contact(p.getContact())
                .educationPeriod(p.getEducationPeriod())
                .educationLocation(extract(p.getEducationLocation(), lang))
                .completionCriteria(extract(p.getCompletionCriteria(), lang))
                .benefits(extractList(p.getBenefits(), lang))
                .schedule(p.getSchedule())
                // ✅ 강사 매핑 추가
                .instructorName(p.getInstructorName())
                .instructorProfileImage(p.getInstructorProfileImage())
                .instructorBio(extractString(p.getInstructorBio(), lang)) // ✅ 수정
                .instructorBio2(extractListSafe(p.getInstructorBio2(), lang)) // ✅ 수정
                .build();
    }

    private static String extract(Map<String, ?> json, String lang) {
        if (json == null) return null;
        Object value = json.get(lang);
        if (value == null) value = json.get("ko");
        return value != null ? value.toString() : null;
    }

    private static String extractString(Object jsonObj, String lang) {
        if (jsonObj == null) return null;
        if (jsonObj instanceof Map<?, ?> map) {
            Object value = map.get(lang);
            if (value == null) value = map.get("ko");
            return value != null ? value.toString() : null;
        }
        return jsonObj.toString();
    }

    private static List<String> extractList(Map<String, List<String>> json, String lang) {
        if (json == null) return null;
        List<String> value = json.get(lang);
        if (value == null) value = json.get("ko");
        return value;
    }

    private static List<String> extractListSafe(Object jsonObj, String lang) {
        if (jsonObj == null) return null;
        if (jsonObj instanceof Map<?, ?> map) {
            Object value = map.get(lang);
            if (value == null) value = map.get("ko");
            if (value instanceof List<?> list) {
                return list.stream().map(Object::toString).toList();
            }
        }
        return null;
    }
}
