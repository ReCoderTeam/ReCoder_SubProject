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
    public String showSendMailPage() {

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

    //내가 보낸 메일 읽기
    @GetMapping("/sentMails")
    public String sentMails(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                            Model model,
                            HttpSession session) {
        // 로그인 유저 정보 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 로그인 정보 확인
        if (loginUser == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String senderEmpId = loginUser.getEmpId();
        int limit = 14;
        int offset = (currentPage - 1) * limit;

        // 보낸 메일 목록 조회
        List<MailDTO> sentMails = mailService.getSentMails(senderEmpId, limit, offset);

        // 전체 보낸 메일 수 조회
        int totalMails = mailService.getSentMailsCount(senderEmpId);

        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalMails / limit);

        // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
        int startNumber = (totalPages - currentPage) * limit + 1;
        for (int i = 0; i < sentMails.size(); i++) {
            // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
            sentMails.get(i).setRowNum(startNumber + (sentMails.size() - 1 - i));
        }

        // 이전 페이지와 다음 페이지 계산
        int prevPage = Math.max(1, currentPage - 1);
        int nextPage = Math.min(totalPages, currentPage + 1);

        // 모델에 데이터 추가
        model.addAttribute("sentMails", sentMails);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("currentItemPage", "sentMails");

        return "mail/sentMails";
    }

    // 받은 메일 페이지
    @GetMapping("/receivedMails")
    public String receivedMails(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            Model model,
            HttpSession session) {

        // 로그인 유저 정보 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 로그인 정보 확인
        if (loginUser == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String receiverEmpId = loginUser.getEmpId();
        int limit = 14;
        int offset = (currentPage - 1) * limit;

        // 받은 메일 목록 조회
        List<MailDTO> receivedMails = mailEmployeeService.getReceivedMails(receiverEmpId, limit, offset);

        // 전체 받은 메일 수 조회
        int totalMails = mailEmployeeService.getReceivedMailsCount(receiverEmpId);

        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalMails / limit);

        // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
        int startNumber = (totalPages - currentPage) * limit + 1;
        for (int i = 0; i < receivedMails.size(); i++) {
            // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
            receivedMails.get(i).setRowNum(startNumber + (receivedMails.size() - 1 - i));
        }

        // 이전 페이지와 다음 페이지 계산
        int prevPage = Math.max(1, currentPage - 1);
        int nextPage = Math.min(totalPages, currentPage + 1);

        // 모델에 데이터 추가
        model.addAttribute("receivedMails", receivedMails);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("currentPageItem", "receivedMails");

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
