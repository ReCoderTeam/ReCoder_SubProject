package com.ohgiraffers.refactorial.mail.controller;

import com.ohgiraffers.refactorial.mail.model.dto.MailEmployeeDTO;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.service.MailEmployeeService;
import com.ohgiraffers.refactorial.mail.service.MailService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/mail")
public class MailController {

    private MailService mailService;

    private MailEmployeeService mailEmployeeService;

    @Autowired
    public MailController(MailService mailService, MailEmployeeService mailEmployeeService) {
        this.mailService = mailService;
        this.mailEmployeeService = mailEmployeeService;
    }

    @PostMapping("/searchReceiver")
    public String searchReceiver(@RequestParam("searchReceiverInput") String searchReceiverInput, Model model) {
        List<MailEmployeeDTO> employees = mailEmployeeService.searchEmployees(searchReceiverInput);  // 검색 로직
        model.addAttribute("employees", employees);  // 검색 결과를 모델에 추가
        return "mail/sendMail :: searchResults";  // sendMail.html에서 searchResults Fragment를 렌더링
    }

    // 메일 쓰기 페이지로 이동
    @GetMapping("/sendMail")
    public String showSendMailPage() {

        return "/mail/sendMail";
    }

    // 메일 보내기
    @PostMapping("/sendMail")
    public String sendMail(@ModelAttribute MailDTO mailDTO, HttpSession session, Model model) {

        // 로그인 유저 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 발신자 정보 설정
        String senderEmpId = loginUser.getEmpId(); // LoginUserDTO 의 필드 값을 사용
        model.addAttribute("senderEmpId", senderEmpId);

        // 메일 서비스 호출
        mailService.sendMail(mailDTO);
        System.out.println(mailDTO);
        return "redirect:/mail/sentMails";
    }

    //내가 보낸 메일 읽기
    @GetMapping("/sentMails")
    public String sentMails(Model model, HttpSession session) {
        // 로그인 유저 정보 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String senderEmpId = loginUser.getEmpId();

        // 보낸 메일 목록을 모델에 추가
        List<MailDTO> sentMails = mailService.getSentMails(senderEmpId);

        // Model sentMails 데이터가 제대로 추가되었는지 확인
        model.addAttribute("sentMails", sentMails);

        return "/mail/sentMails";
    }

    //내가 받은 메일 읽기
    @GetMapping("/receivedMails")
    public String receivedMails(Model model, HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String receiverEmpId = loginUser.getEmpId();

        // 내가 받은 메일 목록을 모델에 추가
        List<MailDTO> receivedMails = mailService.getReceivedMails(receiverEmpId);

        // Model receivedMails 데이터가 제대로 추가되었는지 확인
        model.addAttribute("receivedMails", receivedMails);

        return "/mail/receivedMails";
    }
}
