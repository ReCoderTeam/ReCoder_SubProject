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

import java.util.List;

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
    model.addAttribute("currentPage", "adminInquiryList"); // 현재 페이지 설정
    return "inquiry/adminInquiryList";
}

    // 미답변 문의 가져오기
    @GetMapping("/admin/adminInquiryNoAnswerList")
    public String getAllNoAnswerInquires(Model model) {
        List<InquiryDTO> getAllNoAnswerInquires = adminInquiryService.getAllNoAnswerInquires();
        model.addAttribute("getAllNoAnswerInquires", getAllNoAnswerInquires);
        model.addAttribute("currentPage", "adminInquiryNoAnswerList"); // 현재 페이지 설정
        return "inquiry/adminInquiryNoAnswerList";
    }

    // 답변 문의 가져오기
    @GetMapping("/admin/adminInquiryAnswerList")
    public String getAllAnswerInquires(Model model) {
        List<InquiryDTO> getAllAnswerList = adminInquiryService.getAllAnswerList();
        model.addAttribute("getAllAnswerList", getAllAnswerList);
        model.addAttribute("currentPage", "adminInquiryAnswerList"); // 현재 페이지 설정
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

    // 답변 등록
//    @PostMapping("/admin/adminInquiryAnswer")
//    @ResponseBody
//    public String answerInquiry(@RequestParam("iqrValue") String iqrValue, @RequestParam("answerDetail") String answerDetail) {
//        System.out.println("iqrValue: " + iqrValue);
//        System.out.println("answerDetail: " + answerDetail);
//
//        boolean success = adminInquiryService.answerInquiry(iqrValue, answerDetail);
//        System.out.println("답변 등록 성공 여부: " + success); // 추가 로그
//        return success ? "success" : "fail";
//    }

    @PostMapping("/admin/adminInquiryAnswer")
    public ModelAndView answerInquiry(
            @RequestParam("iqrValue") String iqrValue,
            @RequestParam("answerDetail") String answerDetail) {

        boolean success = adminInquiryService.answerInquiry(iqrValue, answerDetail);

        if (success) {
            return new ModelAndView("redirect:/admin/adminInquiryDetail?iqrValue=" + iqrValue);
        } else {
            ModelAndView mav = new ModelAndView("inquiry/adminInquiryDetail");
            mav.addObject("error", "답변 등록에 실패했습니다.");
            return mav;
        }
    }

    // 미답변 문의

}
