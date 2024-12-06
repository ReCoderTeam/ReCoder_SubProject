package com.ohgiraffers.refactorial.approval.controller;

import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/approvals") // 공통 경로 설정
public class ApprovalController {

    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }


    @GetMapping("approvalWaiting")
    public String waitingPage(){
    return "/approvals/approvalWaiting";
}

    @GetMapping("approvalPage")
    public String paymentPage(){
    return "approvals/approvalPage";
}

    @GetMapping("searchEmployee")
    public String searchEmployeeController(@RequestParam("name") String name, Model model){
        List<EmployeeDTO> employees = approvalService.searchEmployee(name);

        model.addAttribute("employees",employees);

        return "approvals/searchEmployee";
    }



}
