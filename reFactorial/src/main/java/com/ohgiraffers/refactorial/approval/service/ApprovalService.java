package com.ohgiraffers.refactorial.approval.service;

import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalService {


    private final EmployeeMapper employeeMapper;

    public ApprovalService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeDTO> searchEmployee(String name) {
        return employeeMapper.searchEmployee(name);
    }
}
