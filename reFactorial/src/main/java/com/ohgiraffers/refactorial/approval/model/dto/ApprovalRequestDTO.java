package com.ohgiraffers.refactorial.approval.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ApprovalRequestDTO {

    private String title; // 결재 제목
    private String category; // 문서 분류
    private int attachment; // 첨부 파일 여부
    private String firstApprover;  // 최초 승인자
    private String midApprover;    // 중간 승인자
    private String finalApprover;  // 최종 승인자
    private List<String> referrers; // 참조자 리스트
    private int creatorId; // 작성자 ID 추가
    private String content;
    private String leaveType;       // 휴가유형 (연차, 반차)
    private LocalDate leaveDate; // 휴가 날짜

    private List<MultipartFile> postfile;

}
