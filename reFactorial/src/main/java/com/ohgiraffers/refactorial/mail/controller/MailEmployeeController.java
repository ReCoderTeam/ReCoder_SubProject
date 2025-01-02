package com.ohgiraffers.refactorial.mail.controller;

import com.ohgiraffers.refactorial.mail.model.dto.MailEmployeeDTO;
import com.ohgiraffers.refactorial.mail.service.MailEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/mail")
public class MailEmployeeController {

    private final MailEmployeeService mailEmployeeService;

    @Autowired
    public MailEmployeeController(MailEmployeeService mailEmployeeService) {
        this.mailEmployeeService = mailEmployeeService;
    }

    @PostMapping("/searchReceiver")
    @ResponseBody
    public List<MailEmployeeDTO> searchReceiver(@RequestParam(value = "searchReceiverInput", required = false) String searchReceiverInput) {
        if (searchReceiverInput != null && !searchReceiverInput.isEmpty()) {
            return mailEmployeeService.searchMailEmployees(searchReceiverInput);
        } else {
            return mailEmployeeService.getAllMailEmployees(); // 모든 직원 목록을 반환하는 메서드
        }
    }
}
