package com.ohgiraffers.refactorial.approval.model.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class EmployeeDTO {

    private String id;             // emp_id
    private String name;           // emp_name
    private String socialNumber;   // emp_no
    private String address;        // emp_address
    private String email;          // emp_email
    private Date joinedDate;       // emp_joined
    private String annualLeave;    // annual_leave
    private String password;       // emp_pwd
    private String profile;        // profile
    private String phone;          // emp_phone
    private int deptCode;          // dept_code
    private int positionValue;     // position_value
    private String viewAuth;       // view_auth
    private String deptName;      // 부서명
    private String positionName;  // 직책명

}
