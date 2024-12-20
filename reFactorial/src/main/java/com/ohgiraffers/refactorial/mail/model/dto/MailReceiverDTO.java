package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailReceiverDTO {
    private int id;                // 고유 ID
    private String emailId;            // 메일 고유 ID (참조)
    private String receiverEmpId;      // 수신자 사원번호
    private Boolean readStatus;        // 수신자별 메일 확인 상태
}