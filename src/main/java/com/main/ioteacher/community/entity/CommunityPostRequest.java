package com.main.ioteacher.community.entity;

import com.main.ioteacher.community.entity.CommunityPost.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityPostRequest {
    private String userId;   // ✅ 추가됨
    private String title;
    private String content;
    private String imageUrl;
    private Category category;
}



