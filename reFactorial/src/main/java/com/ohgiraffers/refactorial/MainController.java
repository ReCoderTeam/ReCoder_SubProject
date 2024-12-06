package com.ohgiraffers.refactorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/user")
    public String mainControll(){
        return "index";
    }

    @GetMapping("/booking")
    public String bookingPage() {
        return "/booking/booking";
    }

    @GetMapping("/inquiry")
    public String inquiryPage() {
        return "/inquiry/inquiry";
    }

    @GetMapping("/approvalMain")
    public String approvalMainController(){
        return "approvals/approvalMain";
    }

    @GetMapping("/notification")
    public String notification() {
        return "board/notification";
    }

    @GetMapping("sharedWork")
    public String sharedWork(){
        return "sharedWork";
    }
  
    @GetMapping("/auth/login")
    public void loginPage(){};
}

