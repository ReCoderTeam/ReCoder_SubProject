package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailDTO {
    private String emailId;
    private String emailTitle;
    private String emailContent;
    private String emailAttachment;
    private byte readStatus;
    private Date sentDate;
    private byte trashDate;
    private String senderEmpId;
    private String receiverEmpId;
}

