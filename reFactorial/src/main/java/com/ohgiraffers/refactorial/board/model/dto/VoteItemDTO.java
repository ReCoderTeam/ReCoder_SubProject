package com.ohgiraffers.refactorial.board.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteItemDTO {

    private int itemId;          // 항목 고유 ID
    private String postId;       // 게시물 ID
    private String itemTitle;    // 항목 제목

}