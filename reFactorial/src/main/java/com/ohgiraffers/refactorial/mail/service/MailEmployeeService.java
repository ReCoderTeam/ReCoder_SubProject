package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dao.MailEmployeeMapper;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dto.MailEmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailEmployeeService {

    private final MailEmployeeMapper mailEmployeeMapper;

    @Autowired
    public MailEmployeeService(MailEmployeeMapper mailEmployeeMapper) {
        this.mailEmployeeMapper = mailEmployeeMapper;
    }

    public List<MailEmployeeDTO> searchMailEmployees(String searchReceiverInput) {
        return mailEmployeeMapper.searchMailEmployees(searchReceiverInput);
    }

    public List<MailEmployeeDTO> getAllMailEmployees() {
        return mailEmployeeMapper.getAllMailEmployees();
    }

    // 보낸 메일 목록 조회
    public List<MailDTO> getSentMails(String senderEmpId) {
        return mailEmployeeMapper.selectSentMails(senderEmpId);
    }

    // 받은 메일 목록 조회
    public List<MailDTO> getReceivedMails(String receiverEmpId, int limit, int offset) {
        return mailEmployeeMapper.selectReceivedMails(receiverEmpId, limit, offset);
    }

    public int getReceivedMailsCount(String receiverEmpId) {
        return mailEmployeeMapper.selectReceivedMailsCount(receiverEmpId);
    }



}
