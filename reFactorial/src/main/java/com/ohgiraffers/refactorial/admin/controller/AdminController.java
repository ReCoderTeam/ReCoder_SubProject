package com.ohgiraffers.refactorial.admin.controller;

import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.admin.model.service.AdminService;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberService ms;

    // 사원 페이지
    @GetMapping("employee")
    public String adminAddEmployee(){ return "admin/admin_employee"; }

    @GetMapping("getAllEmployee")
    @ResponseBody
    public Map<String, Object> getAllEmployee(@RequestParam int sendDept, @RequestParam String sendSearchEmpName){
        Map<String, Object> result = new HashMap<>();

        if (sendSearchEmpName.trim().isEmpty() || sendSearchEmpName == null){
            sendSearchEmpName = null;
        }

        List<LoginUserDTO> userList = adminService.getAllEmployee(sendDept,sendSearchEmpName);

        System.out.println("sendDept = " + sendDept);
        System.out.println("sendSearchEmpName = " + sendSearchEmpName);

        result.put("userList",userList);

        return result;
    }

    @GetMapping("addEmployeeFragment")
    public String addEmployeeFragment(){
        return "admin/addEmployee";
    }

    @GetMapping("modifyEmpInfo")
    @ResponseBody
    public Map<String,Object> modifyEmpInfo( @RequestParam String empId){
        LoginUserDTO user = ms.findUserId(empId);

        Map<String, Object> result = new HashMap<>();

        result.put("ModifyUser", user);

        return result;
    }

    @PostMapping("modifyEmpInfo")
    public ModelAndView modifyEmpInfoUpdate(ModelAndView mv, @ModelAttribute UserDTO userDTO){

        if (userDTO.getEmpPwd() == null || userDTO.getEmpPwd().trim().isEmpty()) {
            userDTO.setEmpPwd(null);
        }

        Integer result = adminService.modifyEmpInfoUpdate(userDTO);
        
        if (result > 0){
            System.out.println("업데이트 완료");
        }

        mv.setViewName("admin/admin_employee");

        return mv;
    }

    // 특정 회원 정보 수정할때 나타나는 페이지 addEmployee 활용
    @PostMapping("addEmployeeFragment")
    public String updateEmployeeFragment(@RequestBody Map<String, Object> res, Model model){

         Map<String, LoginUserDTO> user = (Map<String, LoginUserDTO>) res.get("ModifyUser");

        System.out.println("user = " + user);

        model.addAttribute("ModifyUser", user);

        return "/admin/addEmployee";
    }

    @GetMapping("getByDateAtt")
    @ResponseBody
    public Map<String, Object> getByDateAtt(@RequestParam String selectedDay,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam String searchDept,
                                            @RequestParam String searchEmpName){

        System.out.println("searchDept = " + searchDept);
        System.out.println("searchEmpName = " + searchEmpName);
        
        // 전체 데이터의 개수
        int totalRecords = adminService.getTotalCountByDateAtt(selectedDay, searchDept, searchEmpName);

        System.out.println("totalRecords = " + totalRecords);
        
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        // 건너뛸 갯수
        int offset = (page - 1) * size;

        List<AttendanceDTO> getByDateAttList = adminService.getByDateAtt(selectedDay,offset,size, searchDept, searchEmpName);

        Map<String, Object> result = new HashMap<>();
        result.put("items",getByDateAttList);
        result.put("totalPages",totalPages);

        return result;
    }

    @PostMapping("modifyEmpAtt")
    @ResponseBody
    public Map<String, Object> modifyEmpAtt (@RequestBody Map<String , Object> res){

        Map<String, Object> result = new HashMap<>();

        String empId = String.valueOf( res.get("empId"));
        String attDate = String.valueOf( res.get("attDate"));
        String selectedStatus = String.valueOf( res.get("selectedStatus"));

        Integer updateInt = adminService.modifyEmpAtt(empId,attDate,selectedStatus);

        if (updateInt > 0){
            System.out.println("근태가 수정되었습니다.");
        }

        return result;
    }

    // 예약자 관리 페이지
    @GetMapping("tktreserve")
    public String adminTktreserve(){ return "/admin/admin_tktreserve"; }

    @GetMapping("getTktReserve")
    @ResponseBody
    public Map<String, Object> getTktReserve(@RequestParam(defaultValue = "")  String selectedDay){

        System.out.println("selectedDay = " + selectedDay);

        Map<String, Object> resultData = new HashMap<>();

        List<TktReserveDTO> result = adminService.getTktReserve(selectedDay);

        resultData.put("resultList",result);

        return resultData;
    }

}
