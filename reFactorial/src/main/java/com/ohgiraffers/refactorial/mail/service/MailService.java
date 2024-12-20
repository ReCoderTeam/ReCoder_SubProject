package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dao.MailMapper;
import com.ohgiraffers.refactorial.mail.model.dto.MailReceiverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MailService {

    private MailMapper mailMapper;

    @Autowired
    public MailService(MailMapper mailMapper) {
        this.mailMapper = mailMapper;
    }

    // 메일 보내기
    public void sendMail(MailDTO mailDTO) {

        if (mailDTO.getReceiverEmpIds() == null || mailDTO.getReceiverEmpIds().isEmpty()) {
            throw new IllegalArgumentException("수신자가 없습니다."); // 예외를 던지거나 로깅을 할 수 있습니다.
        }

        // ID 생성 및 중복 방지 로직
        Set<String> generatedIds = new HashSet<>();
        String emId;
        do {
            emId = "EM" + String.format("%05d", (int) (Math.random() * 100000));
        } while (!generatedIds.add(emId)); // 중복이 아니면 Set에 추가

        // 공통 메일 ID 설정
        mailDTO.setEmailId(emId);


        // 메일 저장
        mailMapper.sendMail(mailDTO); // 메일 정보 저장

        // 수신자 정보 저장
        if (mailDTO.getReceiverEmpIds() != null) {
            for (String receiverEmpId : mailDTO.getReceiverEmpIds()) {
                MailReceiverDTO receiverDTO = new MailReceiverDTO();
                receiverDTO.setEmailId(emId);
                receiverDTO.setReceiverEmpId(receiverEmpId);
                receiverDTO.setReadStatus(false); // 기본적으로 읽지 않음 상태
                mailMapper.saveReceiver(receiverDTO); // 수신자 정보 저장
            }
        }
    }

    // 내가 보낸 메일
    public List<MailDTO> getSentMails(String senderEmpId) {
        List<MailDTO> sentMails = mailMapper.getSentMails(senderEmpId);

        return sentMails;
    }

    // 내가 받은 메일
    public List<MailDTO> getReceivedMails(String receiverEmpId) {
        List<MailDTO> receivedMails = mailMapper.getReceivedMails(receiverEmpId);
        return receivedMails;
    }
}
