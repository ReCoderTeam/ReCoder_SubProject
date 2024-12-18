package com.ohgiraffers.refactorial.mail.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDTO {

    private String empId;
    private String empName;
    private String deptCode;
    private String positionValue;
    private String empEmail;
}
