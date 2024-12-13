package com.ohgiraffers.refactorial.attendance.controller;

import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.attendance.service.AttendanceService;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance/*")
public class AttendanceController {

    @Autowired
    private AttendanceService as;

    @GetMapping("getTodayAttendance")
    @ResponseBody
    public Map<String, Object> getTodayAttendance(HttpSession session){

        Map<String, Object> res = new HashMap<>();

        UserDTO LoginUserInfo = (UserDTO) session.getAttribute("LoginUserInfo");

        String userId = LoginUserInfo.getEmpId();

        AttendanceDTO attendance = as.getTodayAttendance(userId);

        if (attendance == null){
            res.put("attStatus","근무시간 외");
        } else{
            res.put("attStatus",attendance.getAttStatus());
        }

        return res;
    }
    
    @PostMapping("getattendance")
    @ResponseBody
    public List<AttendanceDTO> getAttendance(HttpSession session,@RequestBody Map<String, String> request){
        
        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        String searchDate = String.valueOf( request.get("currentStart"));

        LocalDateTime startDateTime = LocalDateTime.parse(searchDate.replace("Z", ""));

        // 번달 받은 값에 1달 더하고 LocalDate 형태로 형변환
        LocalDate startDate = startDateTime.plusMonths(1).toLocalDate();

        // 해당 월의 첫날
        LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);

        // 해당 월의 마지막 날
        LocalDate lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<AttendanceDTO> attendanceList = as.getAttendance(empId,firstDayOfMonth,lastDayOfMonth);

        return attendanceList;
    }
}
