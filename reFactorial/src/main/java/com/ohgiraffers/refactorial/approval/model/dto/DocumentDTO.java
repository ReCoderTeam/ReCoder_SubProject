package com.ohgiraffers.refactorial.approval.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class DocumentDTO {

        private int rowNum;
        private String pmId;         // 문서 ID
        private String title;        // 제목
        private Date createDate;     // 생성일
        private String category;     // 카테고리
        private String status;       // 상태
        private String attachment;   // 첨부파일 경로
        private String creator;      // 작성자 (사원번호)
        private String approvers;    // 승인자 리스트 (Comma-separated)
        private String referrers;    // 참조자 리스트 (JSON String)
        private String creatorName;  // 작성자 이름 추가
        private String content;
        private String fileUrl;
        private String categoryName; // 변환된 카테고리 이름
        private String leaveType; // 휴가 유형 (연차, 반차)
        private LocalDate leaveDate; // 휴가 날짜


        // categoryName 필드의 getter 정의
        public String getCategoryName() {
                switch (this.category) {
                        case "category1":
                                return "품의서";
                        case "category2":
                                return "지출결의서";
                        case "category3":
                                return "휴가신청서";
                        default:
                                return "알 수 없는 카테고리";
                }
        }

    }






