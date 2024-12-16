package com.ohgiraffers.refactorial.approval.controller;

import com.ohgiraffers.refactorial.approval.model.dto.ApprovalRequestDTO;
import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/approvals") // 공통 경로 설정
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserMapper userMapper; // UserMapper 주입

    @Autowired
    public ApprovalController(ApprovalService approvalService,UserMapper userMapper) {

        this.approvalService = approvalService;
        this.userMapper = userMapper;
    }


    @GetMapping("approvalPage")
    public String paymentPage(){

        return "/approvals/approvalPage";
}


    @GetMapping("searchEmployee")
    public String searchEmployeeController(@RequestParam("name") String name, Model model){
        List<EmployeeDTO> employees;
        if (name != null && !name.isEmpty()) {
            // 입력된 이름을 기반으로 검색
            employees = approvalService.searchByName(name);
        } else {
            // 이름이 없으면 전체 리스트 반환
            employees = approvalService.findAllEmployees();
        }

        // 부서명과 직책명을 추가로 조회하여 설정
        for (EmployeeDTO employee : employees) {
            String deptName = userMapper.findDeptName(employee.getDeptCode());
            String positionName = userMapper.findPositionName(employee.getPositionValue());
            employee.setDeptName(deptName);
            employee.setPositionName(positionName);
        }


        model.addAttribute("employees",employees);

        return "/approvals/searchEmployee";
    }

    @GetMapping("searchReferrers")
    public String searchReferrersController(@RequestParam(value = "name", required = false) String name, Model model) {
        List<EmployeeDTO> referrers;
        if (name != null && !name.isEmpty()) {
            referrers = approvalService.searchByReferrersPageName(name);
        } else {
            referrers = approvalService.findAllReferrers();
        }

        // 부서와 직책 이름 설정
        for (EmployeeDTO referrer : referrers) {
            String deptName = userMapper.findDeptName(referrer.getDeptCode());
            String positionName = userMapper.findPositionName(referrer.getPositionValue());
            referrer.setDeptName(deptName);
            referrer.setPositionName(positionName);
        }


        model.addAttribute("referrers", referrers);
        return "/approvals/searchReferrers";
    }

    @PostMapping("/submitApproval")
    public String submitApproval(@ModelAttribute ApprovalRequestDTO approvalRequestDTO,
                                 @RequestParam("file") MultipartFile file,
                                 Model model,
                                 HttpSession session) {

        System.out.println("상신된 참조자 리스트: " + approvalRequestDTO.getReferrers()); // 폼에서 넘어온 참조자 확인

        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String creatorId = user.getEmpId();

        // 승인자들의 이름 리스트 가져오기
        List<String> approverNames = List.of(
                        approvalRequestDTO.getFirstApprover(),
                        approvalRequestDTO.getMidApprover(),
                        approvalRequestDTO.getFinalApprover()
                ).stream()
                .filter(name -> name != null && !name.trim().isEmpty()) // 이름이 null이거나 공백인 경우 필터링
                .toList();

        // 참조자 리스트 확인
        List<String> referrers = approvalRequestDTO.getReferrers();
        System.out.println("참조자 리스트: " + referrers);  // 참조자 리스트가 제대로 전달되는지 확인

        if (approverNames.isEmpty()) {
            model.addAttribute("errorMessage", "승인자를 최소 한 명 이상 선택해야 합니다.");
            return "/approvals/approvalPage";
        }

        // 이름으로 emp_id를 조회
        List<String> approverIds = approverNames.stream()
                .map(name -> approvalService.findEmpIdByName(name)) // 이름을 기반으로 emp_id 조회
                .filter(id -> id != null && !id.trim().isEmpty()) // null 또는 빈 값 필터링
                .toList();

        if (approverIds.isEmpty()) {
            model.addAttribute("errorMessage", "유효한 승인자가 없습니다.");
            return "/approvals/approvalPage";
        }

        // 1. 결재문서 저장
        String pmId = approvalService.saveApproval(approvalRequestDTO, creatorId);

        // 2. 승인자 저장 (emp_id 사용)
        approvalService.saveApprovers(pmId, approverIds);

        // 3. 참조자 저장 (이름을 기반으로 ID로 변환 후 저장)
        List<String> referrerIds = approvalService.findEmpIdsByNames(referrers); // 이름을 기반으로 emp_id로 변환
        approvalService.saveReferrers(pmId, referrerIds);

        // 4. 승인자 ID를 이름으로 변환하여 모델에 추가
        List<String> approverNamesFromIds = approvalService.findEmpNamesByIds(approverIds); // ID를 이름으로 변환
        model.addAttribute("approverNames", approverNamesFromIds);  // 이름을 모델에 추가

        // 5. 참조자 ID를 이름으로 변환하여 모델에 추가
        List<String> referrerNamesFromIds = approvalService.findEmpNamesByIds(referrerIds); // ID를 이름으로 변환
        model.addAttribute("referrerNames", referrerNamesFromIds);  // 이름을 모델에 추가

        // 6. 첨부파일 저장
        if (!file.isEmpty()) {
            // 업로드할 파일 이름과 경로 설정
            String fileName = file.getOriginalFilename();
            String uploadDirPath = "C:/uploads";  // 파일 저장 경로 (절대 경로로 설정)

            // 디렉토리가 존재하지 않으면 생성
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();  // 디렉토리 생성
            }

            String filePath = uploadDirPath + "/" + fileName; // 파일 경로
            long fileSize = file.getSize();
            String fileType = file.getContentType();

            // 파일을 서버에 저장
            File destinationFile = new File(filePath);  // 서버에 저장될 파일 경로
            try {
                file.transferTo(destinationFile);  // 파일 업로드
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "파일 업로드 실패");
                return "/approvals/approvalPage";  // 파일 업로드 실패 시 처리
            }

            // 파일 정보를 DB에 저장
            approvalService.saveApprovalFile(pmId, fileName, filePath, fileSize, fileType);
        }

        return "/approvals/approvalMain";
    }






    // 대기 중
    @GetMapping("waiting")
    public String getApprovalWaiting(@RequestParam(value = "page", defaultValue = "1") int currentPage,Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return "redirect:/login";
        }

        // emp_id를 로그로 확인
        String loggedInEmpId = user.getEmpId();
        System.out.println("현재 로그인한 사용자 ID: " + loggedInEmpId);

        int limit = 14;  // 한 페이지에 14개 문서
        int offset = (currentPage - 1) * limit;  // offset 계산

        // 대기 중 문서 조회
        List<DocumentDTO> waitingDocs = approvalService.getWaitingDocuments(loggedInEmpId, limit, offset);


        int totalDocuments = approvalService.getWaitingCount(loggedInEmpId);
        int totalPages = (int) Math.ceil((double) totalDocuments / limit);  // 총 페이지 수 계산

        // 문서 번호 설정 (현재 페이지에 맞는 번호)
        for (int i = 0; i < waitingDocs.size(); i++) {
            waitingDocs.get(i).setRowNum(totalDocuments - offset - i);  // 번호는 현재 페이지를 기준으로 설정
        }

        // 이전 페이지 번호 계산
        int prevPage = (currentPage - 1 < 1) ? 1 : currentPage - 1;
        // 다음 페이지 번호 계산
        int nextPage = (currentPage + 1 > totalPages) ? totalPages : currentPage + 1;

        model.addAttribute("documents", waitingDocs);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);


        return "/approvals/waiting";
    }



    @GetMapping("referenceDocuments")
    public String getReferenceDocuments(@RequestParam(defaultValue = "1") int currentPage, Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();

        int limit = 14;  // 한 페이지에 14개 문서
        int offset = (currentPage - 1) * limit;  // offset 계산

        // 참조자 문서 조회 (limit, offset 추가)
        List<DocumentDTO> referenceDocs = approvalService.getReferenceDocuments(loggedInEmpId, limit, offset);

        // 참조 문서 목록 출력 (디버깅)
        System.out.println("참조 문서 목록: " + referenceDocs);

        // 최신 글이 위로 정렬되도록 번호를 매기기
        int totalCount = referenceDocs.size();
        for (int i = 0; i < referenceDocs.size(); i++) {
            referenceDocs.get(i).setRowNum(totalCount - i); // 최신 글일수록 높은 번호
        }

        // 총 문서 개수로 페이지 수 계산
        int totalDocuments = approvalService.getTotalReferenceDocuments(loggedInEmpId);
        int totalPages = (int) Math.ceil((double) totalDocuments / limit);  // 총 페이지 수 계산

        // 문서 번호 설정 (현재 페이지에 맞는 번호)
        for (int i = 0; i < referenceDocs.size(); i++) {
            referenceDocs.get(i).setRowNum(totalDocuments - offset - i);  // 번호는 현재 페이지를 기준으로 설정
        }

        // 이전 페이지 번호 계산
        int prevPage = (currentPage - 1 < 1) ? 1 : currentPage - 1;
        // 다음 페이지 번호 계산
        int nextPage = (currentPage + 1 > totalPages) ? totalPages : currentPage + 1;

        model.addAttribute("documents", referenceDocs);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        return "approvals/referenceDocuments";
    }



    @GetMapping("myDocuments")
    public String getMyDocuments(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();
        int limit = 14;  // 한 페이지에 14개 문서
        int offset = (currentPage - 1) * limit;  // offset 계산

        // 작성자가 작성한 문서 가져오기
        List<DocumentDTO> myDocuments = approvalService.getMyDocuments(loggedInEmpId, limit, offset);

        // 전체 문서 개수 가져오기
        int totalDocuments = approvalService.getMyDocumentsCount(loggedInEmpId);
        int totalPages = (int) Math.ceil((double) totalDocuments / limit);  // 총 페이지 수 계산

        // 문서 번호 설정 (현재 페이지에 맞는 번호)
        for (int i = 0; i < myDocuments.size(); i++) {
            myDocuments.get(i).setRowNum(totalDocuments - offset - i);  // 번호는 현재 페이지를 기준으로 설정
        }

        // 이전 페이지 번호 계산
        int prevPage = (currentPage - 1 < 1) ? 1 : currentPage - 1;
        // 다음 페이지 번호 계산
        int nextPage = (currentPage + 1 > totalPages) ? totalPages : currentPage + 1;

        model.addAttribute("documents", myDocuments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "/approvals/myDocuments";
    }

    // 결제문서 상세페이지 조회
    @GetMapping("Detail/{pmId}")
        public String getApprovalDetail(@PathVariable("pmId") String pmId, Model model){

        // pmId에 해당하는 결제 문서 정보 조회
        DocumentDTO document = approvalService.getDocumentById(pmId);
        System.out.println("Approvers: " + document.getApprovers());
        System.out.println("Referrers: " + document.getReferrers());

        System.out.println("Fetched document: " + document); // 데이터 확인용 로그

        // 문서가 없으면 에러 메시지 반환
        if (document == null) {
                model.addAttribute("errorMessage", "해당 결제 문서를 찾을 수 없습니다.");
            return "errorPage"; // 에러 페이지로 리디렉션 (errorPage.html)
        }

        model.addAttribute("document",document);

        return "approvals/Detail";

    }











}
