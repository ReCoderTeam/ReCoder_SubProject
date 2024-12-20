package com.ohgiraffers.refactorial.board.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
// 투표 결과 DTO
public class VoteResultDTO {

    private String voteResultId;    // 투표 결과 ID ()
    private String empId;        // 투표한 사원의 ID
    private Integer itemId;          // 투표한 항목의 ID

}
