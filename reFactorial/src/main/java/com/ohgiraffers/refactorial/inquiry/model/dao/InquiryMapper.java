package com.ohgiraffers.refactorial.inquiry.model.dao;

import com.ohgiraffers.refactorial.inquiry.model.dto.InquiryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InquiryMapper {


    // 문의 보내기
    void sendInquiry(InquiryDTO inquiryDTO);

    // 보낸 문의들
    List<InquiryDTO> sentInquiries(String senderEmpId);

    // 문의 상세 페이지
    InquiryDTO selectInquiryDetail(String iqrValue);

    // 문의 삭제 메소드 추가
    void deleteInquiry(String iqrValue);

    InquiryDTO findByIqrValue(String iqrValue);
}
