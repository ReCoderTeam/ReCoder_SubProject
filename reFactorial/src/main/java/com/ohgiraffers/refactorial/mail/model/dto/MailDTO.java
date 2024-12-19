package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailDTO {
    private String emailId; // 메일 고유 ID
    private String emailTitle; // 메일 제목
    private String emailContent; // 메일 내용
    private boolean readStatus; // 메일 확인 상태
    private LocalDate sentDate; // 발송 날짜
    private boolean trashStatus; // 휴지통 이동 여부
    private String senderEmpId; // 발신자 사원번호
    private List<String> receiverEmpId; // 수신자 사원번호
}

