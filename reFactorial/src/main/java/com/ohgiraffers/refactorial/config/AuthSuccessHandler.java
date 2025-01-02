package com.ohgiraffers.refactorial.config;

import com.ohgiraffers.refactorial.attendance.service.AttendanceService;
import com.ohgiraffers.refactorial.auth.model.AuthDetails;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private AttendanceService attendenceService;

    @Override
    @ModelAttribute("LoginUserInfo")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        LoginUserDTO user = authDetails.getUser();

        request.getSession().setAttribute("LoginUserInfo", user);

        String saveIdParam = request.getParameter("saveId");
        boolean rememberMe = (saveIdParam != null && saveIdParam.equals("true"));

        String memberId = user.getEmpId();

//        System.out.println("LoginUserInfo = " + user);

        // 사번 저장 관려
        if (rememberMe) {
            Cookie cookie = new Cookie("member_id", memberId);
            cookie.setMaxAge(604800); // 7일 동안 유지
            cookie.setPath("/"); // 전체 도메인에서 유효
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("member_id", "");
            cookie.setMaxAge(0); // 쿠키 삭제
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        // 로그인과 동시에 근태 체크
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        boolean att = time.isBefore(LocalTime.of(9, 0));

        String attStatus = null;

        if (att){
            attStatus = "정상 출근";
        } else{
            attStatus = "지각";
        }

        AttendanceDTO attendance = new AttendanceDTO(date,time,attStatus,user.getEmpId());
        attendenceService.addEmpAttendance(attendance);

        response.sendRedirect("/user");
    }


}
