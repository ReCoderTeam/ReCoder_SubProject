package com.ohgiraffers.refactorial.user.controller;

import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/*")
public class UserController {

    private MemberService memberService;

    @Autowired
    public UserController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("addEmployee")
    public ModelAndView addEmployee(ModelAndView mv, @ModelAttribute UserDTO userDTO){

        System.out.println("userDTO.getEmpId() = " + userDTO.getEmpId());

        Integer result = memberService.addEmployee(userDTO);

        String message = null;

        if (result == null){        // id값 중복
            message = "중복된 회원이 존재합니다.";
        } else if (result == 0){        // insert 구문이 실행되다가 실패
            message = "서번 내부에서 오류가 발생했습니다.";
            mv.setViewName("/admin/admin_employee");
        } else if (result >= 1) {
            message = "회원 가입이 완료되었습니다.";
            mv.setViewName("/admin/admin_employee");
        }

        mv.addObject("message", message);

        return mv;
    }

    // 비밀번호 일치 여부 확인
    @PostMapping(value = "user/matchPW", produces = "application/json; charset=UTF-8;")
    @ResponseBody
    public Boolean matchPW(@RequestBody Map<String, String> request, HttpSession session){

        String msg = null;
        boolean matchStatus =false;

        String insertPW = request.get("presentPW");

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String currentPW = user.getEmpPwd();

        matchStatus = memberService.pwMatch(insertPW,currentPW);

        // 서버 응답으로 반환할 결과
        Map<String, Object> response = new HashMap<>();
        response.put("matchStatus", matchStatus);

        return matchStatus;
    }

    @PostMapping("user/changePW")
    public ModelAndView changePW(ModelAndView mv,@RequestParam String changePW, HttpSession session){

        String msg = null;

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        Integer result = memberService.changePw(changePW, empId);


        if (result > 0){
            msg = "비밀번호 변경 성공";
            session.invalidate();
            mv.setViewName("/auth/login");
        } else {
            msg = "비밀번호 변경 실패";
            mv.setViewName("/myPage/myPage");
        }

        mv.addObject("msg",msg);

        return mv;
    }

    @PostMapping(value = "updatePersonalInfo", produces = "application/json; charset=UTF-8;")
    @ResponseBody
    public Map<String, Object> updatePersonalInfo(@RequestBody Map<String, String> request, HttpSession session){

        String email = request.get("email");
        String phone = request.get("phone");
        String address = request.get("address");

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String userId = user.getEmpId();

        Integer result = memberService.updatePersonalInfo(email,phone,address,userId);

        if (result > 0) {
            if (email != null) user.setEmpEmail(email); // 변경된 이메일 저장
            if (phone != null) user.setEmpPhone(phone); // 변경된 전화번호 저장
            if (address != null) user.setEmpAddress(address); // 변경된 주소 저장

            session.setAttribute("LoginUserInfo", user); // 세션 갱신
        }

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        return response;
    }

    @PostMapping("getNameById")
    @ResponseBody
    public Map<String, Object> getNameById (@RequestBody Map<String,Object> res){
        Map<String, Object> result = new HashMap<>();

        String empId = String.valueOf(res.get("empId"));

        String empName = memberService.getNameById(empId);

        result.put("empName",empName);

        return result;
    }
}
