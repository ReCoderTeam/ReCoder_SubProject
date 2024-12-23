package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailEmployeeDTO {
        private String empId;             // emp_id
        private String empName;              // emp_name
        private String empNo;      // emp_no
        private String empAddress;           // emp_address
        private String empEmail;             // emp_email
        private Date empJoinedDate;          // emp_joined
        private String annualLeave;       // annual_leave
        private String empPassword;          // emp_pwd
        private String profile;           // profile
        private String empPhone;             // emp_phone
        private int deptCode;             // dept_code
        private int positionValue;        // position_value
        private String viewAuth;          // view_auth
}
