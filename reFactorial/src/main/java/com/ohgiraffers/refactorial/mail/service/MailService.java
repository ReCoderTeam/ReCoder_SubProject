package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dao.MailMapper;
import com.ohgiraffers.refactorial.mail.model.dto.MailReceiverDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class MailService {

    private MailMapper mailMapper;
    private UploadFileService uploadFileService;

    @Autowired
    public MailService(MailMapper mailMapper, UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
        this.mailMapper = mailMapper;
    }

    // 메일 보내기
    public void sendMail(MailDTO mailDTO, List<MultipartFile> mailFileList) throws IOException {
        if (mailDTO.getReceiverEmpIds() == null || mailDTO.getReceiverEmpIds().isEmpty()) {
            throw new IllegalArgumentException("수신자가 없습니다.");
        }

        String emId = mailDTO.getEmailId(); // 메일 ID 사용

        // 파일 업로드
        if (!mailFileList.isEmpty()) {
            mailDTO.setMailfile(mailFileList);
            mailDTO.setAttachment(1);
            uploadFileService.upLoadFile(mailFileList, emId);  // emId를 mappingId로 사용
        }

        // 메일 저장 (tbl_mail 에 insert)
        mailMapper.sendMail(mailDTO); // 메일 정보 저장

        // 수신자 정보 저장 (tbl_mail_receivers에 insert)
        if (mailDTO.getReceiverEmpIds() != null) {
            for (String receiverEmpId : mailDTO.getReceiverEmpIds()) {
                MailReceiverDTO receiverDTO = new MailReceiverDTO();
                receiverDTO.setEmailId(emId);
                receiverDTO.setReceiverEmpId(receiverEmpId);
                receiverDTO.setReadStatus(false);  // 읽지 않은 상태로 초기화
                mailMapper.saveReceiver(receiverDTO);  // 수신자 정보 저장
            }
        }
    }


    // 내가 보낸 메일
    // 기존 메서드 (파라미터 1개)
    public List<MailDTO> getSentMails(String senderEmpId) {
        List<MailDTO> sentMails = mailMapper.getSentMails(senderEmpId);

        // 각 메일에 대한 수신자 정보 추가
        for (MailDTO mailDTO : sentMails) {
            List<String> receiverEmpIds = mailMapper.getReceiverEmpIds(mailDTO.getEmailId());
            mailDTO.setReceiverEmpIds(receiverEmpIds);
        }

        return sentMails;
    }

    // 새로 추가할 페이지네이션용 메서드 (파라미터 3개)
    public List<MailDTO> getSentMails(String senderEmpId, int limit, int offset) {
        List<MailDTO> sentMails = mailMapper.getSentMailsPaginated(senderEmpId, limit, offset);

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



    // 상세 페이지
    public MailDTO getMailDetail(String emailId) {
        MailDTO mailDetail = mailMapper.getMailDetail(emailId);
        if (mailDetail != null) {
            List<String> receiverEmpIds = mailMapper.getReceiverEmpIds(emailId);
            mailDetail.setReceiverEmpIds(receiverEmpIds);
        }
        return mailDetail;
    }


    public List<String> getReceiverEmpIds(String emailId) {
        return mailMapper.getReceiverEmpIds(emailId);
    }


    public int getSentMailsCount(String senderEmpId) {
        return mailMapper.getSentMailsCount(senderEmpId);
    }
}
