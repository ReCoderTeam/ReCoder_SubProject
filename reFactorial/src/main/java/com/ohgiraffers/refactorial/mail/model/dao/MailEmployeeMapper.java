package com.ohgiraffers.refactorial.mail.model.dao;

import com.ohgiraffers.refactorial.mail.model.dto.MailEmployeeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MailEmployeeMapper {

    // 이름으로 검색
    List<MailEmployeeDTO> searchMailEmployeesByName(String searchReceiverInput);

    // 모든 직원 조회
    List<MailEmployeeDTO> getAllMailEmployees();
}
