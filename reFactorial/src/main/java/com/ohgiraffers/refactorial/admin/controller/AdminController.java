package com.ohgiraffers.refactorial.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.admin.model.service.AdminService;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @Autowired
    private SpringTemplateEngine templateEngine;

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

        Map<String, Object> resultData = new HashMap<>();

        List<TktReserveDTO> result = adminService.getTktReserve(selectedDay);

        resultData.put("resultList",result);

        return resultData;
    }

    @PostMapping("/admin/extractPDF")
    public void extractPDF(@RequestParam List<String> select_tkt ,HttpServletResponse response) throws UnsupportedEncodingException {
        // selectedTickets는 JSON 문자열로 받아옵니다.

        System.out.println("select_tkt = " + select_tkt);


        // PDF 생성 로직 등 처리...
        List<TktReserveDTO> reserveDataList = new ArrayList<>();

        for (String reserveId : select_tkt){
            TktReserveDTO reserveData = adminService.getReserveById(reserveId);

            if (reserveData != null){

                reserveData.setTktReservePhone(reserveData.getTktReservePhone().replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"));

                reserveDataList.add(reserveData);
            }
        }

        System.out.println("reserveDataList = " + reserveDataList);

        // 데이터 가지고 thymleaf 랑 결합할 준비하기
        Context context = new Context();
        context.setVariable("reserveDataList",reserveDataList);

        // 해당 위치 html에 데이터 가져가서 매칭 시키고 string 형태로 가져오기
        String htmlContent = templateEngine.process("admin/pdfFile/resultPDF", context);

        // PDF 파일 이름 설정
        String filename = "ticket-detailsTest";
        String encodedFilename = URLEncoder.encode(filename, "UTF-8");

        // 응답 헤더 설정
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename + ".pdf");

        try(OutputStream os = response.getOutputStream()){
            ITextRenderer renderer = new ITextRenderer();

            String baseUrl = FileSystems
                    .getDefault()
                    .getPath("src", "main", "resources")
                    .toUri()
                    .toURL()
                    .toString();

            renderer.getFontResolver()
                    .addFont(
                            new ClassPathResource("static/font/NanumGothic.ttf").getURL().toString(),
                            BaseFont.IDENTITY_H,
                            BaseFont.EMBEDDED
                    );
            renderer.setDocumentFromString(htmlContent, baseUrl);
            renderer.layout();
            renderer.createPDF(os);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
