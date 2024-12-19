package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dao.MailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // ID 생성 및 중복 방지 로직
        Set<String> generatedIds = new HashSet<>();
        String emId;
        do {
            emId = "EM" + String.format("%05d", (int) (Math.random() * 100000));
        } while (!generatedIds.add(emId)); // 중복이 아니면 Set에 추가


        // 공통 메일 ID 설정
        mailDTO.setEmailId(emId);

        mailMapper.sendMail(mailDTO);
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
