package com.main.ioteacher.mypage.controller;

import com.main.ioteacher.mypage.entity.MyPageCourseResponse;
import com.main.ioteacher.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /** ✅ 로그인 유저의 수강내역 전체 조회 */
    @GetMapping("/enrollments")
    public List<MyPageCourseResponse> getMyEnrollments() {
        return myPageService.getMyEnrollments();
    }
}
