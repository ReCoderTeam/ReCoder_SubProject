package com.ohgiraffers.refactorial.inquiry.controller;

import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.inquiry.model.dto.InquiryDTO;
import com.ohgiraffers.refactorial.inquiry.service.InquiryService;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

    private InquiryService inquiryService;
    private UploadFileService uploadService;

    @Autowired
    public InquiryController(InquiryService inquiryService, UploadFileService uploadService) {
        this.uploadService = uploadService;
        this.inquiryService = inquiryService;
    }

    // 문의 작성 페이지로 이동
    @GetMapping("/sendInquiry")
    public String showInquiryPage(Model model) {
        model.addAttribute("currentPage", "inquiry"); // 1:1 문의하기 페이지
        return "inquiry/sendInquiry";
    }


    // 1:1 문의 내역 페이지로 이동
    @GetMapping("/inquiryList")
    public String inquiry(Model model, HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        if (loginUser == null) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        String senderEmpId = loginUser.getEmpId();
        List<InquiryDTO> sentInquiries = inquiryService.sentInquiries(senderEmpId);

        model.addAttribute("sentInquiries", sentInquiries);
        model.addAttribute("currentPage", "inquiryList"); // 문의 내역 페이지

        return "inquiry/inquiryList";
    }


    // 문의 상세 조회
    @GetMapping("/detail")
    public String inquiryDetail(@RequestParam("iqrValue") String iqrValue, Model model) {
        InquiryDTO inquiryDetail = inquiryService.getInquiryDetail(iqrValue);

        if(inquiryDetail.getAttachment() == 1 ){
            List<UploadFileDTO> uploadFileList = uploadService.findFileByMappingId(iqrValue);
            if(!uploadFileList.isEmpty()){
                model.addAttribute("attachmentFileList",uploadFileList);
            }
        }
        model.addAttribute("inquiryDetail", inquiryDetail);
        return "inquiry/inquiryDetail";
    }

    @GetMapping("deleteInquiry")
    public String deleteInquiry(@RequestParam("iqrValue") String iqrValue) {
        inquiryService.deleteInquiry(iqrValue);
        return "redirect:/inquiry/inquiryList";  // 삭제 후 목록 페이지로 이동
    }
}
