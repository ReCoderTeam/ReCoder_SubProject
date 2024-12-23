package com.ohgiraffers.refactorial.mail.controller;

import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
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


    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
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
        String senderEmpId = loginUser.getEmpId();
        mailDTO.setSenderEmpId(senderEmpId);  // 발신자 정보 설정

        // 수신자 정보가 없을 경우 예외 처리 또는 안내
        if (mailDTO.getReceiverEmpIds() == null || mailDTO.getReceiverEmpIds().isEmpty()) {
            model.addAttribute("error", "수신자를 선택해주세요.");
            return "mail/sendMail"; // 다시 메일 보내기 페이지로 이동
        }

        // 메일 서비스 호출
        mailService.sendMail(mailDTO);

        // 리디렉션 후 메일 보내기 화면으로 이동
        return "redirect:/mail/sendMail";
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
        String receiverEmpIds = loginUser.getEmpId();

        // 내가 받은 메일 목록을 모델에 추가
        List<MailDTO> receivedMails = mailService.getReceivedMails(receiverEmpIds);

        // Model receivedMails 데이터가 제대로 추가되었는지 확인
        model.addAttribute("receivedMails", receivedMails);

        return "/mail/receivedMails";
    }

    // 메일 상세 페이지
    @GetMapping("/mailDetail")
    public String mailDetail(Model model) {
        return "/mail/mailDetail";
    }
}
