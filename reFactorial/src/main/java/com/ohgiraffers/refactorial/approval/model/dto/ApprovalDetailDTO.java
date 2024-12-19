package com.ohgiraffers.refactorial.approval.model.dto;


import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApprovalDetailDTO {

    private String pmId;            // 문서 ID
    private String title;           // 제목
    private Date createDate;        // 생성일
    private String category;        // 문서 카테고리
    private String categoryName;    // 카테고리 이름 변환된 값
    private String status;          // 문서 상태 (대기, 진행중, 완료 등)
    private String content;         // 문서 내용
    private String attachment;      // 첨부파일 경로
    private String leaveType;       // 휴가유형 (연차, 반차 등)
    private LocalDate leaveDate; // 휴가 날짜

    private String creatorId;       // 작성자 ID
    private String creatorName;     // 작성자 이름
    private String creatorDept;     // 작성자 부서 이름

    private List<String> approvers; // 승인자 리스트 (이름 혹은 ID)
    private List<String> referrers; // 참조자 리스트 (이름 혹은 ID)
}
