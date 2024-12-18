package com.ohgiraffers.refactorial.board.model.dto;

import com.ohgiraffers.refactorial.common.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDTO {

    private String empId;            // 사원번호
    private String empName;          // 이름
    private String empNo;            // 주민번호
    private String empAddress;       // 주소
    private String empEmail;         // 이메일
    private String empJoined;        // 입사일
    private String annualLeave;      // 연차
    private String usedAnnualLeave;  // 사용연차
    private String empPwd;           // 비밀번호
    private String profile;          // 프로필사진
    private String empPhone;         // 전화번호
    private int deptCode;            // 부서코드
    private int positionValue;       // 직책값
    private UserRole viewAuth;       // 권한

}