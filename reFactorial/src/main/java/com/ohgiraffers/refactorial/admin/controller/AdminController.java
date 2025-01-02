package com.ohgiraffers.refactorial.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.ohgiraffers.refactorial.addressBook.model.dto.FactoryDTO;
import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.admin.model.service.AdminService;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.service.BoardService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import com.ohgiraffers.refactorial.zasaPage.model.dto.ProductDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDateTime;
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

    @Autowired
    private BoardService boardService;

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

        return "admin/addEmployee";
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
    public String adminTktreserve(){ return "admin/admin_tktreserve"; }

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


        // PDF 생성 로직 등 처리
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
                            new ClassPathResource("NanumGothic.ttf").getURL().toString(),
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

    @GetMapping("product")
    public String adminProduct(){
        return "admin/admin_product";
    }

    @PostMapping("addProduct")
    @ResponseBody
    public String addProduct(@RequestBody ProductDTO productDTO, HttpSession session) {
        // 세션에서 로그인된 사용자 가져오기
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            // 로그인 정보가 없으면 실패 메시지 반환
            return "로그인 정보가 없습니다. 다시 로그인해주세요.";
        }

        // 로그인된 사용자의 emp_id 설정
        productDTO.setEmpId(user.getEmpId());

        // ID 자동 생성
        String newProductId = adminService.generateProductId();
        productDTO.setId(newProductId);


        int result = adminService.addProduct(productDTO);


        if (result > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GetMapping("/products")
    @ResponseBody
    public List<ProductDTO> getAllProducts() {
        return adminService.getAllProducts();
    }

    @GetMapping("/searchProduct")
    @ResponseBody
    public List<ProductDTO> searchProducts(@RequestParam String keyword) {
        return adminService.searchProducts(keyword);
    }

    @GetMapping("/product/{id}")
    @ResponseBody
    public ProductDTO getProductById(@PathVariable String id) {
        return adminService.getProductById(id);
    }

    @PostMapping("/updateProduct")
    @ResponseBody
    public String updateProduct(@RequestBody ProductDTO productDTO,HttpSession session) {
        System.out.println("수정 요청 데이터: " + productDTO);


        int result = adminService.updateProduct(productDTO);

        if (result > 0) {
            return "success";
        } else {
            return "fail";
        }
    }


    @GetMapping("factoryAddressBook")
    public String adminFactoryAddressBook(){
        return "admin/admin_factoryAddressBook";
    }


    @PostMapping("/addFactory")
    @ResponseBody
    public String addFactory(@RequestBody FactoryDTO factoryDTO, HttpSession session){
        System.out.println("FactoryDTO Data: " + factoryDTO);
        System.out.println("Image URL: " + factoryDTO.getFabImage());

        // 로그인된 사용자 가져오기
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return "로그인 정보가 없습니다.";
        }

        // 로그인된 사용자의 empId 설정
        factoryDTO.setEmpId(user.getEmpId());

        // ID 자동 생성
        String newFactoryId = adminService.generateFactoryId();
        System.out.println("newFactoryId: " + newFactoryId);
        factoryDTO.setFabId(newFactoryId);

        // 등록 처리
        int result = adminService.addFactory(factoryDTO);
        return result > 0 ? "success" : "fail";

    }

    @PatchMapping("/updateFactory")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateFactory(@RequestBody FactoryDTO factoryDTO, HttpSession session) {
        System.out.println("수정 요청 데이터: " + factoryDTO);

        Map<String, String> response = new HashMap<>();

        // 세션에서 로그인 사용자 정보 확인
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        if (user == null) {
            response.put("message", "로그인 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // HTTP 401 반환
        }

        // 사용자 ID 설정
        factoryDTO.setEmpId(user.getEmpId());

        // 업데이트 처리
        int result = adminService.updateFactory(factoryDTO);
        if (result > 0) {
            response.put("message", "수정 성공!");
            return ResponseEntity.ok(response); // HTTP 200 반환
        } else {
            response.put("message", "수정 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 반환
        }
    }

    @GetMapping("/factories")
    @ResponseBody
    public List<FactoryDTO> getAllFactories() {
        return adminService.getAllFactories(); // 서비스에서 전체 조회 구현
    }

    @GetMapping("/searchFactory")
    @ResponseBody
    public List<FactoryDTO> searchFactories(@RequestParam String keyword) {
        return adminService.searchFactories(keyword); // 서비스에서 검색 구현
    }

    @GetMapping("/factory/{id}")
    @ResponseBody
    public FactoryDTO getFactoryById(@PathVariable String id) {
        return adminService.getFactoryById(id);
    }

    @GetMapping("notificationEvent")
    public String notificationEventPage(
                                        @RequestParam int categoryCode, Model model,
                                        @RequestParam(value = "page", defaultValue = "1") int currentPage,
                                        @RequestParam(required = false) String searchContents
                                        ){

        int limit = 10; // 한 페이지당 문서 수
        int totalBoardList = boardService.getBoardListCount(categoryCode); // 전체 게시글 개수 가져오기
        int totalPages = totalBoardList > 0 ? (int) Math.ceil((double) totalBoardList / limit) : 1; // 총 페이지 수

        // 현재 페이지 범위 검증
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit; // offset 계산

        List<BoardDTO> postList = boardService.postList(categoryCode, limit, offset, searchContents);

        // 문서 번호 설정
        for (int i = 0; i < postList.size(); i++) {
            postList.get(i).setRowNum(totalBoardList - offset - i);
        }

        // 이전/다음 페이지 설정
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;

        model.addAttribute("currentPage", currentPage); // 현재 페이지 번호
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수
        model.addAttribute("prevPage", prevPage); // 이전 페이지 번호
        model.addAttribute("nextPage", nextPage); // 다음 페이지 번호
        model.addAttribute("totalBoardList", totalBoardList); // 전체 페이지 갯수

        model.addAttribute("postList", postList);    // 템플릿에 값 전달
        model.addAttribute("categoryCode", categoryCode);   // 카테고리코드를 게시물 등록페이지로 이동시키기 위한 셋팅

        return "admin/notificationEvent";
    }

    @GetMapping("postDetail")
    @ResponseBody
    public Map<String, Object> getPostDetail(@RequestParam String postId){
       BoardDTO postDetail =  boardService.postDetail(postId);

       Map<String, Object> result = new HashMap<>();

       result.put("postDetail",postDetail);

       return result;
    }

    @PostMapping("modifyPost")
    @ResponseBody
    public Map<String, Object> modifyPost(@RequestBody Map<String, String> requestData,HttpSession session){
        String postId = requestData.get("postId");
        String postTitle = requestData.get("postTitle");
        String postContent = requestData.get("postContent");
        String postCategory = requestData.get("postCategory");

        System.out.println("postCategory = " + postCategory);

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = user.getEmpId();

        System.out.println("empId = " + empId);

        if (postId != null){
            System.out.println("postId = " + postId);

            BoardDTO post = boardService.postDetail(postId);

            System.out.println("post = " + post);

            LocalDateTime now = LocalDateTime.now();
            post.setPostTitle(postTitle);
            post.setPostContent(postContent);
            post.setPostModificationDate(now);

            System.out.println("업데이트 보내기 전 post = " + post);

            boardService.post(post);
        } else{
            String boardId = "BO" + String.format("%05d", (int) (Math.random() * 100000));     // 5자리 랜덤 문자열 생성(게시물 ID)
            LocalDateTime currentTime = LocalDateTime.now();

            BoardDTO newPost = new BoardDTO();

            newPost.setPostId(boardId);
            newPost.setPostTitle(postTitle);
            newPost.setPostContent(postContent);
            newPost.setEmpId(empId);
            newPost.setCategoryCode(Integer.parseInt(postCategory));
            newPost.setPostCreationDate(currentTime);
            newPost.setPostModificationDate(currentTime);

            System.out.println("newPost = " + newPost);

            boardService.post(newPost);
        }

        Map<String, Object> result = new HashMap<>();
        return result;
    }

    @GetMapping("deletePost")
    @ResponseBody
    public void deletePost(@RequestParam String postId){
        System.out.println("postId = " + postId);
        boardService.postDelete(postId);
    }

}
