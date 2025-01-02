package com.ohgiraffers.refactorial.sharedWork.controller;

import com.ohgiraffers.refactorial.sharedWork.model.dto.SharedWorkDTO;
import com.ohgiraffers.refactorial.sharedWork.service.SharedWorkService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class SharedWorkController {

    private final SharedWorkService sharedService;

    @Autowired
    public SharedWorkController(SharedWorkService sharedService) {
        this.sharedService = sharedService;
    }

    // 전체 업무 조회
    @GetMapping("/event")
    @ResponseBody
    public List<Map<String, String>> getAllSharedWork(HttpSession session) {
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            throw new IllegalStateException("유효한 사용자 세션이 아닙니다.");
        }

        int deptCode = user.getDeptCode();
        List<SharedWorkDTO> workList = sharedService.getAllSharedWork(deptCode);


        // 풀캘린더 형식으로 변환
        return workList.stream()
                .map(work -> Map.of(
                        "id", work.getWorkId(),
                        "title", work.getWorkTitle(),
                        "start", work.getWorkSchedule().toString(),
                        "end", work.getDeadLine() != null ? work.getDeadLine().toString() : null,
                        "description", work.getWorkExplanation(),
                        "color", work.getWorkColor()
                ))
                .toList();
    }


    // 일정 저장
    @PostMapping("/allWork")
    @ResponseBody
    public String saveSharedWork(@RequestBody Map<String, Object> eventData, HttpSession session) {
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");   // 로그인한 유저 정보를 가져옴

        if (user == null) {
            return "유효한 사용자 세션이 아닙니다.";
        }

        // 고유 ID 생성
        String workID = "WO" + String.format("%05d", (int) (Math.random() * 100000));

        SharedWorkDTO sharedWork = new SharedWorkDTO();
        sharedWork.setWorkId(workID);
        sharedWork.setWorkTitle(String.valueOf(eventData.get("workTitle")));  // 업무제목
        sharedWork.setWorkExplanation(String.valueOf(eventData.get("workExplanation")));  // 업무설명
        sharedWork.setDeptCode(user.getDeptCode());     // 유저의 dept 코드 전달
        sharedWork.setWorkColor(String.valueOf(eventData.get("workColor")));    // 색상 전달

        // 날짜 포맷 검증 및 변환
        String workScheduleStr = String.valueOf(eventData.get("workSchedule"));
        String deadLineStr = String.valueOf(eventData.get("deadLine"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (workScheduleStr != null && deadLineStr != null) {
                sharedWork.setWorkSchedule(LocalDate.parse(workScheduleStr, formatter));  // 시작 날짜
                sharedWork.setDeadLine(LocalDate.parse(deadLineStr, formatter));  // 마감 날짜
            } else {
                throw new IllegalArgumentException("날짜 값이 null입니다.");
            }
        } catch (DateTimeParseException e) {
            return "날짜 형식이 잘못되었습니다. 올바른 날짜 형식(yyyy-MM-dd)을 사용해주세요.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();  // "날짜 값이 null입니다."를 반환
        }

        sharedWork.setDeptCode(user.getDeptCode());  // 부서코드 설정

        sharedService.saveSharedWork(sharedWork);

        return "sharedWork/allWork";
    }

    // 일정 삭제
    @PostMapping("/workDelete")
    @ResponseBody
    public String deleteEvent(@RequestBody Map<String, String> requestBody) {
        String workId = requestBody.get("workId"); // JSON에서 workId 추출
        try {
            sharedService.deleteEvent(workId);
            return "삭제 성공";
        } catch (Exception e) {
            return "삭제 실패: " + e.getMessage();
        }
    }

    // 일정 수정
    @PostMapping("/workModify")
    @ResponseBody
    public String updateSharedWork(@RequestBody SharedWorkDTO updatedWork, HttpSession session) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return "유효한 사용자 세션이 아닙니다.";
        }

        updatedWork.setDeptCode(user.getDeptCode());
        boolean isUpdated = sharedService.updateSharedWork(updatedWork);
        if (isUpdated) {
            return "일정이 성공적으로 수정되었습니다.";
        } else {
            return "일정 수정에 실패했습니다.";
        }
    }



}
