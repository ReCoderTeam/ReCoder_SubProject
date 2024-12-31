package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailDTO {
    private String emailId;            // 메일 고유 ID
    private String emailTitle;         // 메일 제목
    private String emailContent;       // 메일 내용
    private LocalDateTime sentDate;             // 발송 날짜
    private boolean trashStatus;        // 휴지통 이동 여부
    private String senderEmpId;        // 발신자 사원번호
    private String receiverEmpId;
    private List<String> receiverEmpIds; // 수신자 목록 (새로 추가된 필드)
    private int attachment;
    private String empName;
    private String deptName;        // 부서명
    private String positionName;    // 직책명
    private String senderEmpName;  // 발신자 사원명
    private String receiverEmpName;// 수신자 사원명

    private List<MultipartFile> mailfile;
    private List<String> mailfileNames;  // 파일명만 저장하는 리스트
    private int rowNum;

}

