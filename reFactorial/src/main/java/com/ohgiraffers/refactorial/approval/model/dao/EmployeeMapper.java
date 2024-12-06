package com.ohgiraffers.refactorial.approval.model.dao;

import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeMapper {

    List<EmployeeDTO> searchEmployee(String name);
}
