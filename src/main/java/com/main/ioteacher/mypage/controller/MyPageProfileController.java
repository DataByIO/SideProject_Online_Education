package com.main.ioteacher.mypage.controller;

import com.main.ioteacher.mypage.entity.ProfileUpdateRequest;
import com.main.ioteacher.mypage.service.MyPageProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mypage/profile")
@RequiredArgsConstructor
public class MyPageProfileController {

    private final MyPageProfileService profileService;

    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest req) {
        profileService.updateProfile(req);
        return ResponseEntity.ok("프로필 정보가 수정되었습니다.");
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = profileService.uploadProfileImage(file);
        return ResponseEntity.ok(imageUrl);
    }
}
