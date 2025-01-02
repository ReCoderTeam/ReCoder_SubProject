package com.ohgiraffers.refactorial.user.controller;

import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/*")
public class UserController {

    private final MemberService memberService;
    private final UploadFileService uploadService;

    @Autowired
    public UserController(MemberService memberService,UploadFileService uploadService) {
        this.memberService = memberService;
        this.uploadService = uploadService;
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
            mv.setViewName("admin/admin_employee");
        } else if (result >= 1) {
            message = "회원 가입이 완료되었습니다.";
            mv.setViewName("admin/admin_employee");
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
            mv.setViewName("auth/login");
        } else {
            msg = "비밀번호 변경 실패";
            mv.setViewName("myPage/myPage");
        }

        mv.addObject("msg",msg);

        return mv;
    }

    @PostMapping(value = "updatePersonalInfo", produces = "application/json; charset=UTF-8;")
    @ResponseBody
    public Map<String, Object> updatePersonalInfo(@RequestParam("email") String email,
                                                  @RequestParam("phone") String phone,
                                                  @RequestParam("address") String address,
                                                  @RequestParam(value = "profileImgList", required = false) List<MultipartFile> profileImgList,
                                                  HttpSession session) throws IOException {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String userId = user.getEmpId();

        String fileImgName = "";

        if (profileImgList !=null && !profileImgList.get(0).isEmpty()){
            uploadService.upLoadFile(profileImgList,userId);
            List<UploadFileDTO> fileInfo = uploadService.findFileByMappingId(userId);

            fileImgName = fileInfo.get(0).getStoreFileName();
        }

        Integer result = memberService.updatePersonalInfo(email,phone,address,userId,fileImgName);

        if (result > 0) {
            if (email != null && !email.equals("null")) user.setEmpEmail(email); // 이메일이 null이 아니고 'null' 문자열이 아닐 때만 저장
            if (phone != null && !phone.equals("null")) user.setEmpPhone(phone); // 전화번호가 null이 아니고 'null' 문자열이 아닐 때만 저장
            if (address != null && !address.equals("null")) user.setEmpAddress(address); // 주소가 null이 아니고 'null' 문자열이 아닐 때만 저장
            if (fileImgName != "") user.setProfile(fileImgName);

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


    @GetMapping("addCheckEvent")
    @ResponseBody
    public void addCheckEvent(HttpSession session){
        LocalDate today = LocalDate.now();
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        System.out.println("today user= " + today + ", " + user.getEmpId());

        int result = memberService.addCheckEvent(today,empId);
        
        if (result>0){
            System.out.println("출석체크 완료");
        } else {
            System.out.println("출석체크 중 문제 발생");
        }
    }

    @GetMapping("getCheckEvent")
    @ResponseBody
    public Map<String,Object> getCheckEvent(HttpSession session){
        LocalDate today = LocalDate.now();
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        int searchResult = memberService.getCheckEvent(today,empId);

        Map<String , Object> returnResult = new HashMap<>();

        if (searchResult > 0){
            System.out.println("출석체크가 이미 되었습니다");
        } else {
            System.out.println("아직 출석체크 안함");
        }

        returnResult.put("result", searchResult);

        return returnResult;
    }


    @GetMapping("getAllCheckEvent")
    @ResponseBody
    public List<String> getAllCheckEvent(@RequestParam String start, @RequestParam String end, HttpSession session){

        LocalDate startDate = LocalDate.parse(start.substring(0,10));
        LocalDate endDate = LocalDate.parse(end.substring(0,10));
//        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 해당 달의 마지막 날 구하기

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        return memberService.getAllCheckEvent(startDate,endDate,empId);
    }
}
