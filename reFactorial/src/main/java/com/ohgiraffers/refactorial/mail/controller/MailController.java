package com.ohgiraffers.refactorial.mail.controller;

import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dto.MailEmployeeDTO;
import com.ohgiraffers.refactorial.mail.service.MailEmployeeService;
import com.ohgiraffers.refactorial.mail.service.MailService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping("/mail")
public class MailController {

    private MailService mailService;
    private UploadFileService uploadService;
    private MailEmployeeService mailEmployeeService;

    @Autowired
    public MailController(MailService mailService, UploadFileService uploadService , MailEmployeeService mailEmployeeService) {
        this.uploadService = uploadService;
        this.mailService = mailService;
        this.mailEmployeeService = mailEmployeeService;
    }

    // 메일 쓰기 페이지로 이동
    @GetMapping("/sendMail")
    public String showSendMailPage(@RequestParam(defaultValue = "1") int currentPage, Model model, HttpSession session) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();
        int limit = 14;
        int offset = (currentPage - 1) * limit;

        List<MailDTO> myDocuments = mailService.getSendMailDocuments(loggedInEmpId, limit, offset,currentPage);
        int totalDocuments = mailService.getTotalSendMailDocuments(loggedInEmpId);
        int totalPages = (int) Math.ceil((double) totalDocuments / limit);

        int startNumber = (totalPages - currentPage) * limit + 1;
        for (int i = 0; i < myDocuments.size(); i++) {
            myDocuments.get(i).setRowNum(startNumber + (myDocuments.size() - 1 - i));
        }

        int prevPage = Math.max(1, currentPage - 1);
        int nextPage = Math.min(totalPages, currentPage + 1);

        model.addAttribute("documents", myDocuments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        return "/mail/sendMail";
    }

    @PostMapping("/sendMail")
    public String sendMail(@ModelAttribute MailDTO mailDTO,
                           @RequestParam("mailFiles") List<MultipartFile> mailFileList,
                           HttpSession session,
                           Model model) throws IOException {

        // 로그인 유저 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 발신자 정보 설정
        mailDTO.setSenderEmpId(loginUser.getEmpId());

        // 수신자 확인
        if (mailDTO.getReceiverEmpIds() == null || mailDTO.getReceiverEmpIds().isEmpty()) {
            model.addAttribute("error", "수신자를 선택해주세요.");
            return "mail/sendMail";
        }

        // 메일 ID 생성 및 설정
        String emId = "EM" + String.format("%05d", (int) (Math.random() * 100000));
        mailDTO.setEmailId(emId); // 이메일 ID 설정

        try {
            // 메일 전송
            mailService.sendMail(mailDTO, mailFileList);  // 메일 전송과 수신자 정보 저장
        } catch (Exception e) {
            model.addAttribute("error", "메일 전송 중 오류가 발생했습니다.");
            return "mail/sendMail";
        }

        return "redirect:/mail/sendMail";  // 메일 전송 후 리다이렉트
    }

    // 메일 답신
    @PostMapping("/reply")
    public String replyMail(@ModelAttribute MailDTO mailDTO, HttpSession session, @RequestParam("mailFiles") List<MultipartFile> mailFileList, Model model) throws IOException {
        // 로그인 유저 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        mailDTO.setSenderEmpId(loginUser.getEmpId());

        // 메일 ID 생성 및 설정
        String emId = "EM" + String.format("%05d", (int) (Math.random() * 100000));
        mailDTO.setEmailId(emId);

        // 원본 메일의 발신자를 수신자로 설정
        String receiverEmpId = mailDTO.getReceiverEmpIds().get(0); // 첫 번째 수신자
        mailDTO.setReceiverEmpIds(Arrays.asList(receiverEmpId)); // 단일 수신자로 설정

        // 메일 서비스 호출
        try {
            mailService.sendMail(mailDTO, mailFileList);
        } catch (Exception e) {
            model.addAttribute("error", "메일 전송 중 오류가 발생했습니다.");
            return "mail/sendMail";
        }

        return "redirect:/mail/sentMails"; // 답신 후 보낸 메일 목록으로 리디렉션
    }

    // 답신 페이지로 이동
    @GetMapping("/reply")
    public String showReplyPage(@RequestParam("emailId") String emailId, Model model) {
        MailDTO originalMail = mailService.getReplyMailDetail(emailId);

        if (originalMail == null) {
            throw new IllegalArgumentException("해당 이메일이 존재하지 않습니다.");
        }

        model.addAttribute("originalMail", originalMail);
        return "mail/replyMail"; // 템플릿 경로 확인
    }

    //내가 보낸 메일 읽기
    @GetMapping("/sentMails")
    public String sentMails(Model model, HttpSession session) {
        // 로그인 유저 정보 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String senderEmpId = loginUser.getEmpId();

        // 보낸 메일 목록을 모델에 추가
        List<MailDTO> sentMails = mailEmployeeService.getSentMails(senderEmpId);

        // Model sentMails 데이터가 제대로 추가되었는지 확인
        model.addAttribute("sentMails", sentMails);

        return "mail/sentMails";
    }

    //내가 받은 메일 읽기
    @GetMapping("/receivedMails")
    public String receivedMails(Model model, HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String receiverEmpIds = loginUser.getEmpId();

        // 내가 받은 메일 목록을 모델에 추가
        List<MailDTO> receivedMails = mailEmployeeService.getReceivedMails(receiverEmpIds);

        // Model receivedMails 데이터가 제대로 추가되었는지 확인
        model.addAttribute("receivedMails", receivedMails);

        return "mail/receivedMails";
    }

    // 메일 상세 페이지
    @GetMapping("/detail")
    public String mailDetail(@RequestParam("emailId") String emailId, Model model) {
        MailDTO mailDetail = mailService.getMailDetail(emailId);
        List<String> mailReceiver = mailService.getReceiverEmpIds(emailId);

        if (mailDetail.getAttachment() == 1) {
            List<UploadFileDTO> uploadFileList = uploadService.findFileByMappingId(emailId);
            if (!uploadFileList.isEmpty()) {
                model.addAttribute("attachmentFileList", uploadFileList);
            }
        }

        model.addAttribute("mailDetail", mailDetail);
        model.addAttribute("mailReceiver", mailReceiver);
        return "mail/mailDetail";
    }

    // 메일 휴지통 페이지
    @GetMapping("/detailBin")
    public String mailDetailBin(@RequestParam("emailId") String emailId, Model model) {
        MailDTO mailDetailBin = mailService.getMailDetailBin(emailId);
        model.addAttribute("mailDetailBin", mailDetailBin);
        return "mail/mailDetailBin";
    }

    // 휴지통 보기
    @GetMapping("/mailBin")
    public String viewMailBin(Model model, HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String senderEmpId = loginUser.getEmpId();

        List<MailDTO> sentMailsBin = mailService.getSentMailsBin(senderEmpId);
        model.addAttribute("sentMailsBin", sentMailsBin);

        return "mail/mailBin";
    }

    // 휴지통으로 보내기
    @PostMapping("/moveToTrash")
    public String moveToTrash(@RequestParam("emailId") String emailId,
                              @RequestParam("receiverEmpId") String receiverEmpId
                              ) {
        // 특정 수신자에 대해 휴지통으로 이동
        if (receiverEmpId != null && !receiverEmpId.isEmpty()) {
            mailService.moveToTrash(emailId, Collections.singletonList(receiverEmpId));
        }
        return "redirect:/mail/mailBin"; // 휴지통 페이지로 리디렉션
    }

    @PostMapping("/removeToTrash")
    public String removeToTrash(@RequestParam("emailId") String emailId) {
        // 메일을 휴지통으로 보내는 서비스 호출
        mailService.removeToTrash(emailId);

        return "redirect:/mail/mailBin"; // 휴지통 페이지로 리디렉션
    }
}
