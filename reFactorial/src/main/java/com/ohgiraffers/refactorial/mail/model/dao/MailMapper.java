package com.ohgiraffers.refactorial.mail.model.dao;

import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.model.dto.MailReceiverDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MailMapper {

    // 메일쓰기
    void sendMail(MailDTO mailDTO);

    // 수신자 정보 저장
    void saveReceiver(MailReceiverDTO receiverDTO);

    // 내가 보낸 메일 읽기
    List<MailDTO> getSentMails(String senderEmpId);

    // 내가 받은 메일 읽기
    List<MailDTO> getReceivedMails(String receiverEmpId);

    // 수신자 가져오기
    List<String> getReceiverEmpIds(String emailId);

    // 상세페이지
    MailDTO getMailDetail(String emailId);

    // 휴지통 상세페이지
    MailDTO getMailDetailBin(String emailId);

    // 휴지통 불러오기
    List<MailDTO> getReceivedMailsBin(String receiverEmpIds);
    List<MailDTO> getSentMailsBin(String senderEmpId);

    // 휴지통 복구 삭제
    void updateTrashRemove(String emailId, int i);

    // 휴지통으로 이동하기
    void updateTrashStatus(Map<String, Object> params);


    int getTotalSendMailDocuments(String empId);

    List<MailDTO> getSendMailDocuments(Map<String, Object> params);

    MailDTO getReplyMailDetail(String emailId);
}
