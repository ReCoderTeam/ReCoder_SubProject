package com.ohgiraffers.refactorial.mail.service;

import com.ohgiraffers.refactorial.mail.model.dao.EmployeeMapper;
import com.ohgiraffers.refactorial.mail.model.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeDTO> searchEmployees(String empName) {
        return employeeMapper.searchEmployees(empName);
    }
}

