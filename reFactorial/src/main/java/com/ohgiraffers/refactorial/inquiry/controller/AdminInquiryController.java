package com.ohgiraffers.refactorial.inquiry.controller;

import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.inquiry.model.dto.InquiryDTO;
import com.ohgiraffers.refactorial.inquiry.service.AdminInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminInquiryController {


    private AdminInquiryService adminInquiryService;
    private UploadFileService uploadService;

    @Autowired
    public AdminInquiryController(AdminInquiryService adminInquiryService, UploadFileService uploadService) {
        this.uploadService = uploadService;
        this.adminInquiryService = adminInquiryService;
    }
// 모든 문의 불러오기
@GetMapping("/admin/adminInquiryList")
public String getAllInquiries(Model model) {
    List<InquiryDTO> getAllInquiries = adminInquiryService.getAllInquiries();
    model.addAttribute("getAllInquiries", getAllInquiries);
    model.addAttribute("currentPageItem", "adminInquiryList"); // 현재 페이지 설정
    return "inquiry/adminInquiryList";
}

    // 미답변 문의 가져오기
    @GetMapping("/admin/adminInquiryNoAnswerList")
    public String getAllNoAnswerInquires(Model model) {
        List<InquiryDTO> getAllNoAnswerInquires = adminInquiryService.getAllNoAnswerInquires();
        model.addAttribute("getAllNoAnswerInquires", getAllNoAnswerInquires);
        model.addAttribute("currentPageItem", "adminInquiryNoAnswerList"); // 현재 페이지 설정
        return "inquiry/adminInquiryNoAnswerList";
    }

    // 답변 문의 가져오기
    @GetMapping("/admin/adminInquiryAnswerList")
    public String getAllAnswerInquires(Model model) {
        List<InquiryDTO> getAllAnswerList = adminInquiryService.getAllAnswerList();
        model.addAttribute("getAllAnswerList", getAllAnswerList);
        model.addAttribute("currentPageItem", "adminInquiryAnswerList"); // 현재 페이지 설정
        return "inquiry/adminInquiryAnswerList";
    }
    // 문의 상세 조회
    @GetMapping("/admin/adminInquiryDetail")
    public String adminInquiryDetail( @RequestParam("iqrValue") String iqrValue, Model model) {
        InquiryDTO adminInquiries = adminInquiryService.adminInquiryDetail(iqrValue);

        if (adminInquiries.getAttachment() == 1) {
            List<UploadFileDTO> uploadFileList = uploadService.findFileByMappingId(iqrValue);
            if (!uploadFileList.isEmpty()) {
                model.addAttribute("attachmentFileList", uploadFileList);
            }
        }

        model.addAttribute("adminInquiries", adminInquiries);
        return "inquiry/adminInquiryDetail";
    }

    @PostMapping("/admin/adminInquiryAnswer")
    public ModelAndView answerInquiry(
            @RequestParam("iqrValue") String iqrValue,
            @RequestParam("answerDetail") String answerDetail) {

        boolean success = adminInquiryService.answerInquiry(iqrValue, answerDetail);

        if (success) {
            ModelAndView mav = new ModelAndView("redirect:/admin/adminInquiryDetail?iqrValue=" + iqrValue);
            mav.addObject("answerDetail", answerDetail);  // 답변 내용 추가
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("inquiry/adminInquiryDetail");
            mav.addObject("error", "답변 등록에 실패했습니다.");
            return mav;
        }
    }

}
