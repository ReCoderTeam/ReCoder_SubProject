package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dao.MailMapper;
import com.ohgiraffers.refactorial.mail.model.dto.MailReceiverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

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

        // 각 메일에 대한 수신자 정보 추가
        for (MailDTO mailDTO : sentMails) {
            List<String> receiverEmpIds = mailMapper.getReceiverEmpIds(mailDTO.getEmailId());
            mailDTO.setReceiverEmpIds(receiverEmpIds);
        }

        return sentMails;
    }

    // 내가 받은 메일
    public List<MailDTO> getReceivedMails(String receiverEmpId) {
        List<MailDTO> receivedMails = mailMapper.getReceivedMails(receiverEmpId);

        // 각 메일에 대한 수신자 정보 추가
        for (MailDTO mailDTO : receivedMails) {
            List<String> receiverEmpIds = mailMapper.getReceiverEmpIds(mailDTO.getEmailId());
            mailDTO.setReceiverEmpIds(receiverEmpIds);
        }

        return receivedMails;
    }


    // 휴지통 보낸 메일 받은 메일
    public List<MailDTO> getReceivedMailsBin(String receiverEmpIds) {
        List<MailDTO> receivedMailsBin = mailMapper.getReceivedMailsBin(receiverEmpIds);

        // 각 메일에 대한 수신자 정보 추가
        for (MailDTO mailDTO : receivedMailsBin) {
            List<String> receiverEmpIdsBin = mailMapper.getReceiverEmpIds(mailDTO.getEmailId());
            mailDTO.setReceiverEmpIds(receiverEmpIdsBin);
        }

        return receivedMailsBin;
    }

    // 수정된 getSentMailsBin 메서드
    public List<MailDTO> getSentMailsBin(String senderEmpId) {
        List<MailDTO> sentMailsBin = mailMapper.getSentMailsBin(senderEmpId);

        // 각 메일에 대한 수신자 정보 추가
        for (MailDTO mailDTO : sentMailsBin) {
            List<String> receiverEmpIdsBin = mailMapper.getReceiverEmpIds(mailDTO.getEmailId());
            mailDTO.setReceiverEmpIds(receiverEmpIdsBin);
        }

        return sentMailsBin;
    }

    // 상세 페이지
    public MailDTO getMailDetail(String emailId) {

        return mailMapper.getMailDetail(emailId);
    }

    // 휴지통으로 보내기
    public void moveToTrash(String emailId, String receiverEmpId) {
        Map<String, Object> params = new HashMap<>();
        params.put("emailId", emailId);
        params.put("receiverEmpId", receiverEmpId);
        params.put("trashStatus", 1);

        mailMapper.updateTrashStatus(params);
    }


    // 휴지통 상세 페이지
    public MailDTO getMailDetailBin(String emailId) {

        return mailMapper.getMailDetailBin(emailId);
    }

    // 휴지통 복구하기
    public void removeToTrash(String emailId) {
        mailMapper.updateTrashRemove(emailId,0); // trashStatus 를 0로 변경 (휴지통으로 이동)
    }

}
