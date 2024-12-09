package com.ohgiraffers.refactorial.approval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/approvals") // 공통 경로 설정
public class ApprovalController {

@GetMapping("approvalWaiting")
    public String waitingPage(){
    return "approvals/approvalWaiting";
}

@GetMapping("approvalPage")
    public String paymentPage(){
    return "approvals/approvalPage";
}
}
