package com.ohgiraffers.refactorial.approval.controller;

import com.ohgiraffers.refactorial.approval.model.dto.*;
import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/approvals") // 공통 경로 설정
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserMapper userMapper; // UserMapper 주입

    @Autowired
    public ApprovalController(ApprovalService approvalService, UserMapper userMapper) {

        this.approvalService = approvalService;
        this.userMapper = userMapper;
    }


    @GetMapping("approvalPage")
    public String paymentPage() {

        return "/approvals/approvalPage";
    }

    @GetMapping("completed")
    public String getCompletedDocuments(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                                        Model model, HttpSession session) {
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();
        int limit = 14; // 한 페이지당 문서 수
        int totalDocuments = approvalService.getCompletedDocumentsCount(loggedInEmpId); // 전체 문서 개수 가져오기

        int totalPages = totalDocuments > 0 ? (int) Math.ceil((double) totalDocuments / limit) : 1; // 최소 1페이지

        // 현재 페이지 범위 검증
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit; // offset 계산

        // 완료된 문서 가져오기
        List<DocumentDTO> completedDocuments = approvalService.getCompletedDocuments(loggedInEmpId, limit, offset);
        if (completedDocuments == null) { // null 방지
            completedDocuments = new ArrayList<>();
        }


        // 문서 번호 설정 (현재 페이지에 맞는 번호)
        for (int i = 0; i < completedDocuments.size(); i++) {
            completedDocuments.get(i).setRowNum(totalDocuments - offset - i);
        }

        // 최신 글이 위로 정렬되도록 번호를 매기기
        int totalCount = completedDocuments.size();
        for (int i = 0; i < completedDocuments.size(); i++) {
            completedDocuments.get(i).setRowNum(totalCount - i); // 최신 글일수록 높은 번호
        }

        // 이전/다음 페이지 번호 설정
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;
        System.out.println("Completed documents: " + completedDocuments);

        // 모델에 데이터 추가
        model.addAttribute("documents", completedDocuments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", currentPage > 1 ? currentPage - 1 : 1);
        model.addAttribute("nextPage", currentPage < totalPages ? currentPage + 1 : totalPages);

        return "/approvals/completed";
    }

    @GetMapping("inProgress")
    public String getInProgressDocuments(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                                         Model model, HttpSession session) {
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();
        int limit = 14; // 한 페이지당 문서 수
        int totalDocuments = approvalService.getInProgressDocumentsCount(loggedInEmpId); // 전체 문서 개수 가져오기
        int totalPages = totalDocuments > 0 ? (int) Math.ceil((double) totalDocuments / limit) : 1;

        // 현재 페이지 범위 검증
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit; // offset 계산


        System.out.println("empId: " + loggedInEmpId);
        System.out.println("limit: " + limit);
        System.out.println("offset: " + offset);

        // 진행 중인 문서 가져오기
        List<DocumentDTO> inProgressDocuments = approvalService.getInProgressDocuments(loggedInEmpId, limit, offset);

        System.out.println("In Progress Documents: " + inProgressDocuments);
        // 문서 번호 설정
        for (int i = 0; i < inProgressDocuments.size(); i++) {
            inProgressDocuments.get(i).setRowNum(totalDocuments - offset - i);
        }

        // 최신 글이 위로 정렬되도록 번호 설정
        int totalCount = inProgressDocuments.size();
        for (int i = 0; i < inProgressDocuments.size(); i++) {
            inProgressDocuments.get(i).setRowNum(totalCount - i);
        }

        // 이전/다음 페이지 설정
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;

        // 모델에 데이터 추가
        model.addAttribute("documents", inProgressDocuments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "/approvals/inProgress";
    }


    @GetMapping("rejected")
    public String getRejectedDocuments(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                                       Model model, HttpSession session) {
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String loggedInEmpId = user.getEmpId();
        int limit = 14; // 한 페이지당 문서 수
        int totalDocuments = approvalService.getRejectedDocumentsCount(loggedInEmpId); // 전체 문서 개수 가져오기
        int totalPages = totalDocuments > 0 ? (int) Math.ceil((double) totalDocuments / limit) : 1;

        // 현재 페이지 범위 검증
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit; // offset 계산

        // 반려된 문서 가져오기
        List<DocumentDTO> rejectedDocuments = approvalService.getRejectedDocuments(loggedInEmpId, limit, offset);

        // 문서 번호 설정
        for (int i = 0; i < rejectedDocuments.size(); i++) {
            rejectedDocuments.get(i).setRowNum(totalDocuments - offset - i);
        }

        // 최신 글이 위로 정렬되도록 번호 설정
        int totalCount = rejectedDocuments.size();
        for (int i = 0; i < rejectedDocuments.size(); i++) {
            rejectedDocuments.get(i).setRowNum(totalCount - i);
        }

        // 이전/다음 페이지 설정
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;

        // 모델에 데이터 추가
        model.addAttribute("documents", rejectedDocuments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "/approvals/rejected";
    }




    @GetMapping("searchEmployee")
    public String searchEmployeeController(@RequestParam("name") String name, Model model) {
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


        model.addAttribute("employees", employees);

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

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String creatorId = user.getEmpId();

        // 승인자 이름 가져오기
        List<String> approverNames = new ArrayList<>();
        if (approvalRequestDTO.getFirstApprover() != null && !approvalRequestDTO.getFirstApprover().isEmpty()) {
            approverNames.add(approvalRequestDTO.getFirstApprover());
        }
        if (approvalRequestDTO.getMidApprover() != null && !approvalRequestDTO.getMidApprover().isEmpty()) {
            approverNames.add(approvalRequestDTO.getMidApprover());
        }
        if (approvalRequestDTO.getFinalApprover() != null && !approvalRequestDTO.getFinalApprover().isEmpty()) {
            approverNames.add(approvalRequestDTO.getFinalApprover());
        }

        // 승인자가 없으면 오류 메시지 반환
        if (approverNames.isEmpty()) {
            model.addAttribute("errorMessage", "최소 한 명의 승인자가 필요합니다.");
            return "/approvals/approvalPage";
        }

        // 이름으로 emp_id 조회
        List<Map<String, Object>> approvers = new ArrayList<>();
        int order = 1;
        for (String approverName : approverNames) {
            String approverId = approvalService.findEmpIdByName(approverName);
            if (approverId != null) {
                approvers.add(Map.of("empId", approverId, "order", order++));
            }
        }

        // 결재문서 저장
        String pmId = approvalService.saveApproval(approvalRequestDTO, creatorId);

        // 휴가유형 처리 (휴가신청서일 경우만)
        if ("category3".equals(approvalRequestDTO.getCategory())) {
            if (approvalRequestDTO.getLeaveType() != null && !approvalRequestDTO.getLeaveType().isEmpty()) {
                approvalService.updateLeaveType(pmId, approvalRequestDTO.getLeaveType());
            } else {
                model.addAttribute("errorMessage", "휴가유형을 선택해야 합니다.");
                return "/approvals/approvalPage";
            }

            // LocalDate를 문자열로 변환
            LocalDate leaveDate = approvalService.getLeaveDateForDocument(pmId);
            if (leaveDate != null) {
                String formattedLeaveDate = leaveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                model.addAttribute("leaveDate", formattedLeaveDate);
            } else {
                model.addAttribute("leaveDate", "휴가 날짜 정보 없음");
            }
        }

        // 승인자 저장 (입력된 승인자만 저장)
        approvalService.saveApprovers(pmId, approvers);

        // 참조자 저장
        List<String> referrers = approvalRequestDTO.getReferrers();
        List<String> referrerIds = approvalService.findEmpIdsByNames(referrers);
        approvalService.saveReferrers(pmId, referrerIds);

        // 첨부파일 저장
        if (!file.isEmpty()) {
            try {
                approvalService.saveApprovalFile(pmId, file.getOriginalFilename(),
                        "C:/uploads/" + file.getOriginalFilename(), file.getSize(), file.getContentType());
                file.transferTo(new File("C:/uploads/" + file.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "파일 업로드 실패");
                return "/approvals/approvalPage";
            }
        }



        return "/approvals/approvalMain";
    }


    // 대기 중
    @GetMapping("waiting")
    public String getApprovalWaiting(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        // emp_id를 로그로 확인
        String loggedInEmpId = user.getEmpId();
        System.out.println("현재 로그인한 사용자 ID: " + loggedInEmpId);

        int limit = 14;  // 한 페이지에 14개 문서
        int offset = (currentPage - 1) * limit;  // offset 계산

        // 대기 중 문서 조회
        List<DocumentDTO> waitingDocs = approvalService.getWaitingDocuments(loggedInEmpId, limit, offset);
        System.out.println("Retrieved waitingDocs: " + waitingDocs);

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
        // 세션에서 로그인 사용자 정보 가져오기
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
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
        // 세션에서 로그인 사용자 정보 가져오기
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
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
    @GetMapping("detail/{pmId}")
    public String getApprovalDetail(@PathVariable("pmId") String pmId, Model model, HttpSession session) {

        // pmId에 해당하는 결재 문서 정보 조회
        DocumentDTO document = approvalService.getDocumentById(pmId);

        if (document == null) {
            model.addAttribute("errorMessage", "해당 결제 문서를 찾을 수 없습니다.");
            return "errorPage"; // 에러 페이지로 리디렉션
        }

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String currentEmpId = user.getEmpId();
        int currentOrder = approvalService.getCurrentApprovalOrder(pmId, currentEmpId); // 현재 승인 순서 조회

        // 작성자 정보 처리
        String creatorName = document.getCreatorName() != null ? document.getCreatorName() : "작성자 정보 없음";

        // 승인자 정보 처리
        List<String> approverIds = document.getApprovers() != null
                ? Arrays.asList(document.getApprovers().split(","))
                : new ArrayList<>();
        String firstApprover = approverIds.size() > 0 ? approvalService.findEmpNameById(approverIds.get(0)) : "승인자 정보 없음";
        String midApprover = approverIds.size() > 1 ? approvalService.findEmpNameById(approverIds.get(1)) : "승인자 정보 없음";
        String finalApprover = approverIds.size() > 2 ? approvalService.findEmpNameById(approverIds.get(2)) : "승인자 정보 없음";

        // 참조자 정보 처리
        List<String> referrerIds = document.getReferrers() != null
                ? Arrays.asList(document.getReferrers().split(","))
                : new ArrayList<>();
        List<String> referrerNames = referrerIds.stream()
                .map(approvalService::findEmpNameById)
                .distinct()
                .collect(Collectors.toList());

        // 첨부파일 정보 처리
        List<String> fileUrls = (document.getFileUrl() != null && !document.getFileUrl().isEmpty())
                ? Arrays.asList(document.getFileUrl().split(",")).stream()
                .map(fileName -> "/files/" + fileName)
                .collect(Collectors.toList())
                : new ArrayList<>();

        document.setCategoryName(document.getCategoryName());

        if ("category3".equals(document.getCategory())) {
            // 휴가 유형 처리
            String leaveType = approvalService.getLeaveTypeForDocument(pmId);
            document.setLeaveType(leaveType);

            // 휴가 날짜 처리
            LocalDate leaveDate = approvalService.getLeaveDateForDocument(pmId);
            document.setLeaveDate(leaveDate);
            model.addAttribute("leaveDate", leaveDate); // 모델에 날짜 추가
        }




        // 모델에 데이터 추가
        model.addAttribute("document", document);
        model.addAttribute("creatorName", creatorName);
        model.addAttribute("firstApprover", firstApprover);
        model.addAttribute("midApprover", midApprover);
        model.addAttribute("finalApprover", finalApprover);
        model.addAttribute("referrerNames", referrerNames);
        model.addAttribute("fileUrls", fileUrls);
        model.addAttribute("currentOrder", currentOrder);
        model.addAttribute("isCurrentApprover", approvalService.isCurrentApprover(pmId, currentEmpId));

        return "/approvals/detail";
    }

    @PostMapping("detail")
    public String handleApprovalAction(@RequestParam("pmId") String pmId,
                                       @RequestParam("action") String action,
                                       @RequestParam(value = "reason", required = false) String reason,
                                       HttpSession session,
                                       Model model) {



        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String currentEmpId = user.getEmpId();

        try {
            switch (action) {
                case "approve":
                    // 현재 사용자가 승인 순서인지 확인
                    if (!approvalService.isCurrentApprover(pmId, currentEmpId)) {
                        model.addAttribute("errorMessage", "현재 승인 순서가 아닙니다.");
                        return "redirect:/approvals/detail/" + pmId;
                    }

                    // 승인 처리
                    approvalService.approve(pmId, currentEmpId);

                    // 최초 승인자인 경우, 나머지 승인자 상태를 '진행 중'으로 변경
                    if (!approvalService.isAllApproversApproved(pmId)) {
                        approvalService.updateRemainingApproversToInProgress(pmId, currentEmpId);
                        return "redirect:/approvals/inProgress"; // 진행 중 페이지로 이동
                    } else {
                        approvalService.updateStatusToCompleted(pmId); // 상태를 '완료'로 변경
                        return "redirect:/approvals/completed"; // 완료 페이지로 이동
                    }


                case "reject":
                    approvalService.reject(pmId, currentEmpId, reason);
                    return "redirect:/approvals/rejected"; // 반려된 경우

                case "finalize":
                    approvalService.finalize(pmId, currentEmpId);
                    approvalService.updateStatusToCompleted(pmId); // 전결 시 바로 완료 처리
                    return "redirect:/approvals/completed"; // 상태가 완료된 경우
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "문서 처리 중 오류가 발생했습니다.");
            return "redirect:/approvals/detail/" + pmId;
        }

        return "redirect:/approvals/detail/" + pmId;
    }

    @GetMapping("detail")
    public String showApprovalDetail(@RequestParam("pmId") String pmId, Model model, HttpSession session) {


        if (pmId == null || pmId.isEmpty()) {
            model.addAttribute("errorMessage", "문서 ID가 제공되지 않았습니다.");
            return "error/400";
        }
        // 세션에서 사용자 정보 확인
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        // 승인 문서 상세 정보 조회
        try {
            ApprovalDetailDTO approvalDetail = approvalService.getApprovalDetail(pmId);
            model.addAttribute("approvalDetail", approvalDetail);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "문서 정보를 불러오는 중 오류가 발생했습니다.");
            return "error/500"; // 오류 페이지로 이동
        }

        System.out.println("Received pmId: " + pmId); // 디버깅 로그 추가
        return "approvals/detail"; // 상세 페이지로 이동
    }



//    @RestController
//    @RequestMapping("/files")
//    public class FileController {


    // 파일 다운로드 (파일 ID로 조회)
    // 파일 다운로드 (파일 ID 기준)
    @GetMapping("/downloadById")
    public ResponseEntity<Resource> downloadFileById(@RequestParam int fileId) {
        try {
            FileDTO file = approvalService.getFileById(fileId);

            if (file == null || file.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(file.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String encodedFileName = URLEncoder.encode(file.getFileName(), "UTF-8").replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 파일 다운로드 (파일 이름으로 조회)
    @GetMapping("/downloadByName")
    public ResponseEntity<Resource> downloadFileByName(@RequestParam String fileName) {
        try {
            FileDTO file = approvalService.getFileByFileName(fileName);

            if (file == null || file.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(file.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}














