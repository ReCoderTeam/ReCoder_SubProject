package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dao.MailEmployeeMapper;
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
        return mailEmployeeMapper.searchMailEmployeesByName(searchReceiverInput);
    }

    public List<MailEmployeeDTO> getAllMailEmployees() {
        return mailEmployeeMapper.getAllMailEmployees();
    }
}
