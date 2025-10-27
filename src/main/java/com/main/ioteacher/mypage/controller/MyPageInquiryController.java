package com.main.ioteacher.mypage.controller;

import com.main.ioteacher.mypage.entity.*;
import com.main.ioteacher.mypage.service.MyPageInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mypage/inquiries")
@RequiredArgsConstructor
public class MyPageInquiryController {

    private final MyPageInquiryService inquiryService;

    @PostMapping
    public InquiryResponse createInquiry(@RequestBody InquiryRequest req) {
        return inquiryService.createInquiry(req);
    }

    @GetMapping
    public List<InquiryResponse> getMyInquiries() {
        return inquiryService.getMyInquiries();
    }

    @PutMapping("/{id}")
    public InquiryResponse updateInquiry(@PathVariable Long id, @RequestBody InquiryRequest req) {
        return inquiryService.updateInquiry(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
    }
}
