package com.main.ioteacher.community.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 작성 요청 DTO
 */
@Getter
@Setter
public class CommentRequest {
    private String userId;
    private String content;
}
