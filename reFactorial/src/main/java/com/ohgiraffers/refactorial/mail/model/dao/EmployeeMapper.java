package com.ohgiraffers.refactorial.mail.model.dao;

import com.ohgiraffers.refactorial.mail.model.dto.EmployeeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    List<EmployeeDTO> searchEmployees(@Param("empName") String empName);
}
