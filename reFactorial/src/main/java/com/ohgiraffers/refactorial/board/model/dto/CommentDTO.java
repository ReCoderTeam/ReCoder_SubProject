package com.ohgiraffers.refactorial.board.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO {

    private String commentContent;              // 댓글 내용
    private int commentId;                      // 댓글고유 IDcomment_creationDate
    private int postId;                          // 게시물 ID
    private String empId;                        // 댓글 작성 사원번호
    private LocalDate commentCreationDate;   // 댓글 등록 시간
}