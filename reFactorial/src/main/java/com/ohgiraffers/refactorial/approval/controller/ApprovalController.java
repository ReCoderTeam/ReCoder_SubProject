    package com.ohgiraffers.refactorial.approval.controller;

    import com.ohgiraffers.refactorial.approval.model.dto.*;
    import com.ohgiraffers.refactorial.approval.service.ApprovalService;
    import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
    import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
    import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
    import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
    import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.FileSystemResource;
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

    import java.io.FileNotFoundException;
    import java.math.BigDecimal;
    import java.net.URLEncoder;
    import java.nio.file.Path;
    import java.nio.file.Paths;


    import java.io.File;
    import java.io.IOException;
    import java.time.LocalDate;
    import java.time.LocalTime;
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
        private final UploadFileService uploadService;


        @Autowired
        public ApprovalController(ApprovalService approvalService, UserMapper userMapper, UploadFileService uploadService) {

            this.approvalService = approvalService;
            this.userMapper = userMapper;
            this.uploadService = uploadService;
        }


        @GetMapping("approvalPage")
        public String paymentPage() {

        return "approvals/approvalPage";
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
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> myDocuments = approvalService.getCompletedDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getCompletedDocumentsCount(loggedInEmpId);
            int totalPages = (int) Math.ceil((double) totalDocuments / limit);

            // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
            int startNumber = (totalPages - currentPage) * limit + 1;
            for (int i = 0; i < myDocuments.size(); i++) {
                // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
                myDocuments.get(i).setRowNum(startNumber + (myDocuments.size() - 1 - i));
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", myDocuments);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);

        return "approvals/completed";
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
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> inProgressDocuments = approvalService.getInProgressDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getInProgressDocumentsCount(loggedInEmpId);
            int totalPages = (int) Math.ceil((double) totalDocuments / limit);

            // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
            int startNumber = (totalPages - currentPage) * limit + 1;
            for (int i = 0; i < inProgressDocuments.size(); i++) {
                // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
                inProgressDocuments.get(i).setRowNum(startNumber + (inProgressDocuments.size() - 1 - i));
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", inProgressDocuments);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);

        return "approvals/inProgress";
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
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> rejectedDocuments  = approvalService.getRejectedDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getInProgressDocumentsCount(loggedInEmpId);
            int totalPages = (int) Math.ceil((double) totalDocuments / limit);

            // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
            int startNumber = (totalPages - currentPage) * limit + 1;
            for (int i = 0; i < rejectedDocuments .size(); i++) {
                // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
                rejectedDocuments .get(i).setRowNum(startNumber + (rejectedDocuments .size() - 1 - i));
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", rejectedDocuments );
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);

        return "approvals/rejected";
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

        return "approvals/searchEmployee";
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
        return "approvals/searchReferrers";
    }

        @PostMapping("/submitApproval")
        public String submitApproval(@ModelAttribute ApprovalRequestDTO approvalRequestDTO,
                                     @RequestParam List<MultipartFile> fileList,
                                     Model model,
                                     HttpSession session) throws IOException {


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
            return "approvals/approvalPage";
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

            // 첨부파일 저장 로직 (있는지 판단)
            approvalRequestDTO.setAttachment(0);

            if (fileList != null && !fileList.isEmpty()) {
                boolean hasValidFile = false;
                for (MultipartFile file : fileList) {
                    if (!file.isEmpty()) {
                        hasValidFile = true;
                        break; // 적어도 하나의 유효한 파일이 있으면 중단
                    }
                }
                if (hasValidFile) {
                    approvalRequestDTO.setAttachment(1); // 유효한 파일이 있을 경우 처리
                }
            }

            // 결재문서 저장
            String pmId = approvalService.saveApproval(approvalRequestDTO, creatorId, fileList);

        // **추가된 휴가 날짜 업데이트 로직**
        if ("category3".equals(approvalRequestDTO.getCategory())) { // 휴가 신청서 분류 확인
            if (approvalRequestDTO.getLeaveDate() != null) {
                // 휴가 날짜 업데이트
                approvalService.updateLeaveDate(pmId, approvalRequestDTO.getLeaveDate());
            } else {
                model.addAttribute("errorMessage", "휴가 날짜를 선택해야 합니다.");
                return "approvals/approvalPage";
            }

            if (approvalRequestDTO.getLeaveType() != null && !approvalRequestDTO.getLeaveType().isEmpty()) {
                // 휴가 유형 업데이트
                approvalService.updateLeaveType(pmId, approvalRequestDTO.getLeaveType());
            } else {
                model.addAttribute("errorMessage", "휴가 유형을 선택해야 합니다.");
                return "approvals/approvalPage";
            }
        }

            // 승인자 저장 (입력된 승인자만 저장)
            approvalService.saveApprovers(pmId, approvers);

            // 참조자 저장
            List<String> referrers = approvalRequestDTO.getReferrers();
            List<String> referrerIds = approvalService.findEmpIdsByNames(referrers);
            approvalService.saveReferrers(pmId, referrerIds);


            return "redirect:/user/approvalMain";
        }


        // 대기 중
        @GetMapping("waiting")
        public String getApprovalWaiting(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpSession session) {
            LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

            if (user == null) {
                model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
                return "redirect:/login";
            }

            String loggedInEmpId = user.getEmpId();
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> waitingDocuments = approvalService.getWaitingDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getWaitingCount(loggedInEmpId);

            // totalPages 계산 수정 - 최소 1페이지 보장
            int totalPages = Math.max(1, (int) Math.ceil((double) totalDocuments / limit));

            // 번호 매기기 수정
            if (!waitingDocuments.isEmpty()) {
                int startNumber = (totalPages - currentPage) * limit + 1;
                for (int i = 0; i < waitingDocuments.size(); i++) {
                    waitingDocuments.get(i).setRowNum(startNumber + (waitingDocuments.size() - 1 - i));
                }
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", waitingDocuments);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);


        return "approvals/waiting";
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
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> myDocuments = approvalService.getReferenceDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getTotalReferenceDocuments(loggedInEmpId);
            int totalPages = (int) Math.ceil((double) totalDocuments / limit);

            // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
            int startNumber = (totalPages - currentPage) * limit + 1;
            for (int i = 0; i < myDocuments.size(); i++) {
                // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
                myDocuments.get(i).setRowNum(startNumber + (myDocuments.size() - 1 - i));
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", myDocuments);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);
            return "/approvals/referenceDocuments";
        }


        @GetMapping("myDocuments")
        public String getMyDocuments(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpSession session) {
            // 세션에서 로그인한 사용자 정보 가져오기

            LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

            if (user == null) {
                model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
                return "redirect:/login";
            }


            String loggedInEmpId = user.getEmpId();
            int limit = 14;
            int offset = (currentPage - 1) * limit;

            List<DocumentDTO> myDocuments = approvalService.getMyDocuments(loggedInEmpId, limit, offset);
            int totalDocuments = approvalService.getMyDocumentsCount(loggedInEmpId);
            int totalPages = (int) Math.ceil((double) totalDocuments / limit);

            // 번호 매기기 수정 - 각 페이지 내에서 아래에서 위로 증가하도록
            int startNumber = (totalPages - currentPage) * limit + 1;
            for (int i = 0; i < myDocuments.size(); i++) {
                // i 대신 (size - 1 - i)를 사용하여 역순으로 번호 매기기
                myDocuments.get(i).setRowNum(startNumber + (myDocuments.size() - 1 - i));
            }

            int prevPage = Math.max(1, currentPage - 1);
            int nextPage = Math.min(totalPages, currentPage + 1);

            model.addAttribute("documents", myDocuments);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("prevPage", prevPage);
            model.addAttribute("nextPage", nextPage);

        return "approvals/myDocuments";
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

            boolean isFinalApprover = approvalService.isFinalApprover(pmId, currentEmpId);
            model.addAttribute("isFinalApprover", isFinalApprover);

            // 참조자 정보 처리
            List<String> referrerIds = document.getReferrers() != null
                    ? Arrays.asList(document.getReferrers().split(","))
                    : new ArrayList<>();
            List<String> referrerNames = referrerIds.stream()
                    .map(approvalService::findEmpNameById)
                    .distinct()
                    .collect(Collectors.toList());


            if ("category3".equals(document.getCategory())) {
                // 휴가 유형 처리
                String leaveType = approvalService.getLeaveTypeForDocument(pmId);
                document.setLeaveType(leaveType);

                // 휴가 날짜 처리
                LocalDate leaveDate = approvalService.getLeaveDateForDocument(pmId);
                document.setLeaveDate(leaveDate);
                model.addAttribute("leaveDate", leaveDate); // 모델에 날짜 추가
            }

            // 첨부파일 처리
            if (document.getAttachment() == 1){
                List<UploadFileDTO> uploadFileList = uploadService.findFileByMappingId(pmId);
                model.addAttribute("attachmentFileList", uploadFileList);
            }



            // 반려자인지 확인하고 반려 이유 가져오기
            String rejectReason = approvalService.getRejectReasonByApprover(pmId, currentEmpId);



            // 모델에 데이터 추가
            model.addAttribute("document", document);
            model.addAttribute("creatorName", creatorName);
            model.addAttribute("firstApprover", firstApprover);
            model.addAttribute("midApprover", midApprover);
            model.addAttribute("finalApprover", finalApprover);
            model.addAttribute("referrerNames", referrerNames);

        model.addAttribute("currentOrder", currentOrder);
        model.addAttribute("isCurrentApprover", approvalService.isCurrentApprover(pmId, currentEmpId));
        model.addAttribute("rejectReason", rejectReason); // 반려 이유 모델에 추가
        model.addAttribute("isRejecter", rejectReason != null); // 반려자인지 여부 확인
        return "approvals/detail";
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

                        // 연차/반차 처리 로직 추가
                        processLeaveIfApplicable(pmId, currentEmpId);


                        // 모든 승인자가 완료되었는지 확인
                        boolean allApproved = approvalService.isAllApproversApproved(pmId);

                        if (allApproved) {
                            return "redirect:/approvals/completed"; // 완료 페이지로 이동
                        } else {
                            return "redirect:/approvals/inProgress"; // 진행 중 페이지로 이동
                        }

                    case "reject":
                        approvalService.reject(pmId, currentEmpId, reason);
                        return "redirect:/approvals/rejected"; // 반려된 경우

                    case "finalize":
                        approvalService.finalize(pmId, currentEmpId);
                        approvalService.updateStatusToCompleted(pmId); // 전결 시 바로 완료 처리

                        // 전결 시 연차/반차 처리 로직 추가
                        processLeaveIfApplicable(pmId, currentEmpId);

                        return "redirect:/approvals/completed"; // 상태가 완료된 경우
                }

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "문서 처리 중 오류가 발생했습니다.");
                return "redirect:/approvals/detail/" + pmId;
            }

            return "redirect:/approvals/detail/" + pmId;
        }

        /**
         * 연차/반차 처리 메서드
         */
        private void processLeaveIfApplicable(String pmId, String empId) {
            DocumentDTO document = approvalService.getDocumentById(pmId);

            if (document != null) {
                String leaveType = document.getLeaveType();
                LocalDate leaveDate = document.getLeaveDate();
                BigDecimal deduction = BigDecimal.ZERO;

                // 문서 작성자(결제 신청자) ID 가져오기
                String creatorId = document.getCreator();

                if ("연차".equals(leaveType)) {
                    deduction = BigDecimal.ONE; // 연차는 -1
                } else if ("반차".equals(leaveType)) {
                    deduction = new BigDecimal("0.5"); // 반차는 -0.5
                }

                if (deduction.compareTo(BigDecimal.ZERO) > 0) {
                    // 결제 신청자 기준으로 연차 업데이트
                    approvalService.updateEmployeeLeave(creatorId, deduction, leaveType);

                    // 근태 기록 생성
                    AttendanceDTO attendanceDTO = new AttendanceDTO();
                    attendanceDTO.setEmpId(creatorId);
                    attendanceDTO.setAttDate(leaveDate);
                    attendanceDTO.setAttTime(LocalTime.of(9, 0)); // 기본 출근 시간
                    attendanceDTO.setAttStatus(leaveType.equals("연차") ? "연차" : "반차");

                    approvalService.insertAttendanceRecord(attendanceDTO);
                }
            } else {
                System.out.println("문서 정보가 없습니다.");
            }
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


            return "approvals/detail"; // 상세 페이지로 이동
        }




    }
















