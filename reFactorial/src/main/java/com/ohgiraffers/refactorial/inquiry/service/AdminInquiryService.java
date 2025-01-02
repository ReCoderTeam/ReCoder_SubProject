package com.ohgiraffers.refactorial.inquiry.service;

import com.ohgiraffers.refactorial.inquiry.model.dao.AdminInquiryMapper;
import com.ohgiraffers.refactorial.inquiry.model.dao.InquiryMapper;
import com.ohgiraffers.refactorial.inquiry.model.dto.InquiryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminInquiryService {

    private InquiryMapper inquiryMapper;
    private AdminInquiryMapper adminInquiryMapperr;

    @Autowired
    public AdminInquiryService(AdminInquiryMapper adminInquiryMapperr, InquiryMapper inquiryMapper) {
        this.adminInquiryMapperr = adminInquiryMapperr;
        this.inquiryMapper = inquiryMapper;
    }

    // 모든 문의 가져오기
    public List<InquiryDTO> getAllInquiries() {
        return adminInquiryMapperr.getAllInquiries();
    }

    // 모든 미답변 문의 가져오기
    public List<InquiryDTO> getAllNoAnswerInquires(){
        return adminInquiryMapperr.getAllNoAnswerInquires();
    }

    // 모든 답변 문의 가져오기
    public List<InquiryDTO> getAllAnswerList(){
        return adminInquiryMapperr.getAllAnswerList();
    }

    // 특정 문의 상세 조회
    public InquiryDTO adminInquiryDetail(String iqrValue) {
        return adminInquiryMapperr.adminInquiryDetail(iqrValue);
    }

    // 문의 답변 처리
    public boolean answerInquiry(String iqrValue, String answerDetail) {
        int rowsAffected = adminInquiryMapperr.updateAnswer(iqrValue, answerDetail);
        return rowsAffected > 0;
    }
}