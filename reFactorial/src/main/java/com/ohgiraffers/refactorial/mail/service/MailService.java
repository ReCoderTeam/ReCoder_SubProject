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
        MailDTO mailDetail = mailMapper.getMailDetail(emailId);
        if (mailDetail != null) {
            List<String> receiverEmpIds = mailMapper.getReceiverEmpIds(emailId);
            mailDetail.setReceiverEmpIds(receiverEmpIds);
        }
        return mailDetail;
    }

    // 휴지통으로 보내기
    // 서비스에서 메일을 각 수신자별로 휴지통으로 보낼 수 있도록 변경
    public void moveToTrash(String emailId, List<String> receiverEmpIds) {
        // 각 수신자에 대해 개별적으로 trash_status 업데이트
        for (String receiverEmpId : receiverEmpIds) {
            Map<String, Object> params = new HashMap<>();
            params.put("emailId", emailId);
            params.put("trashStatus", 1); // 휴지통으로 이동
            params.put("receiverEmpId", receiverEmpId); // 특정 수신자 ID
            mailMapper.updateTrashStatus(params); // DB 업데이트 호출
        }
    }




    // 휴지통 상세 페이지
    public MailDTO getMailDetailBin(String emailId) {

        return mailMapper.getMailDetailBin(emailId);
    }

    // 휴지통 복구하기
    public void removeToTrash(String emailId) {
        mailMapper.updateTrashRemove(emailId,0); // trashStatus 를 0로 변경 (휴지통으로 이동)
    }

    public List<String> getReceiverEmpIds(String emailId) {
        return mailMapper.getReceiverEmpIds(emailId);
    }


    public List<MailDTO> getSendMailDocuments(String empId, int limit, int offset,int currentPage) {
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("limit", limit);
        params.put("offset", offset);

        List<MailDTO> documents = mailMapper.getSendMailDocuments(params);

        int totalDocuments = mailMapper.getTotalSendMailDocuments(empId);
        int totalPages = (int) Math.ceil((double) totalDocuments / limit);

        int startNumber = (totalPages - currentPage) * limit + 1;
        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).setRowNum(startNumber + (documents.size() - 1 - i));
        }

        return documents;
    }


    public int getTotalSendMailDocuments(String empId) {
        return mailMapper.getTotalSendMailDocuments(empId);
    }

    public MailDTO getReplyMailDetail(String emailId) {
        return mailMapper.getReplyMailDetail(emailId);
    }
}
