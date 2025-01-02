    package com.ohgiraffers.refactorial.approval.service;

    import com.ohgiraffers.refactorial.approval.model.dao.ApprovalMapper;
    import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
    import com.ohgiraffers.refactorial.approval.model.dto.*;
    import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
    import com.ohgiraffers.refactorial.fileUploade.model.dao.UploadFileMapper;
    import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
    import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
    import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;


    import java.io.IOException;
    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service

    public class ApprovalService {


        @Autowired
        private ApprovalMapper approvalMapper;

        @Autowired
        private EmployeeMapper employeeMapper;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private UploadFileMapper uploadFileMapper;

        @Autowired
        private UploadFileService uploadService;

        public List<EmployeeDTO> searchByName(String name) {

            return employeeMapper.searchByName(name);
        }

        public List<EmployeeDTO> findAllEmployees() {
            return employeeMapper.findAllEmployees();
        }

        public List<EmployeeDTO> searchByReferrersPageName(String name) {
            return employeeMapper.searchByReferrersPageName(name);
        }

        public List<EmployeeDTO> findAllReferrers() {
            return employeeMapper.findAllReferrers();
        }


        public String saveApproval(ApprovalRequestDTO approvalRequestDTO, String creatorId, List<MultipartFile> fileList) throws IOException {

            Map<String, Object> params = new HashMap<>();

            //5자리 랜덤 문자열 생성
            String pmId = "PM" + String.format("%05d", (int) (Math.random() * 100000));

            params.put("pmId", pmId);
            params.put("title", approvalRequestDTO.getTitle());
            params.put("date", LocalDateTime.now()); // 현재 날짜
            params.put("category", approvalRequestDTO.getCategory());
            params.put("attachment", approvalRequestDTO.getAttachment());
            params.put("creatorId", creatorId); // 작성자 ID 추가
            params.put("content", approvalRequestDTO.getContent()); // 추가된 내용

            // 첨부파일 저장 로직
            if (approvalRequestDTO.getAttachment() == 1){
                System.out.println("파일 저장");
                uploadService.upLoadFile(fileList, pmId);
            }

            approvalMapper.insertPm(params); // 매퍼 호출
            return String.valueOf(params.get("pmId")); // 생성된 pmId 반환
        }


        public void saveApprovers(String pmId, List<Map<String, Object>> approvers) {
            if (approvers != null && !approvers.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("pmId", pmId);

                // 각 승인자에 approvalOrder 값 추가
                List<Map<String, Object>> approverListWithOrder = new ArrayList<>();
                for (int i = 0; i < approvers.size(); i++) {
                    Map<String, Object> approver = new HashMap<>(approvers.get(i));
                    approver.put("approvalOrder", i + 1); // 순서를 1부터 시작하도록 설정
                    approverListWithOrder.add(approver);
                }

                params.put("approvers", approverListWithOrder);
                approvalMapper.insertApprover(params);
            }
        }


        // 참조자 저장
        public void saveReferrers(String pmId, List<String> referrers) {
            if (referrers != null && !referrers.isEmpty()) {
                List<String> uniqueReferrers = new ArrayList<>(new HashSet<>(referrers));
                Map<String, Object> params = new HashMap<>();
                params.put("pmId", pmId);
                params.put("referrers", uniqueReferrers);
                approvalMapper.saveReferrers(params);
            }
        }

        public List<DocumentDTO> getWaitingDocuments(String empId, int limit, int offset) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }


            // 참조자 문서 조회
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);

            return approvalMapper.getWaitingDocuments(params);
        }

        public int getWaitingCount(String empId) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 작성자가 작성한 문서의 총 개수 조회
            return approvalMapper.getWaitingCount(empId);

        }


        public String findEmpIdByName(String name) {
            return userMapper.findEmpIdByName(name); // 이름으로 emp_id 조회
        }

        public List<DocumentDTO> getReferenceDocuments(String empId, int limit, int offset) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 참조자 문서 조회
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);

            return approvalMapper.getReferenceDocuments(params);
        }

        public int getTotalReferenceDocuments(String empId) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }
            return approvalMapper.getTotalReferenceDocuments(empId);
        }


        public List<DocumentDTO> getMyDocuments(String empId, int limit, int offset) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 파라미터를 Map으로 묶어 매퍼에 전달
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);

            // 작성자가 작성한 문서 가져오기 (LIMIT과 OFFSET을 적용한 쿼리 호출)
            List<DocumentDTO> myDocuments = approvalMapper.getMyDocuments(params);


            return myDocuments;  // 결과 반환
        }


        public int getMyDocumentsCount(String empId) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 작성자가 작성한 문서의 총 개수 조회
            return approvalMapper.getMyDocumentsCount(empId);
        }


        public void saveApprovalFile(String pmId, String fileName, String filePath, long fileSize, String fileType) {
            // 파일 정보 저장을 위한 로직
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("pmId", pmId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("filePath", filePath);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileType", fileType);

            // DB에 파일 정보 저장
            approvalMapper.insertApprovalFile(fileInfo);
        }


        public DocumentDTO getDocumentById(String pmId) {

            return  approvalMapper.getDocumentById(pmId);
        }

        public List<String> findEmpIdsByNames(List<String> referrers) {
            // 이름 리스트가 비어 있는지 확인
            if (referrers == null || referrers.isEmpty()) {
                return Collections.emptyList();
            }

            // 이름을 기반으로 emp_id 리스트를 생성
            return referrers.stream()
                    .map(name -> employeeMapper.findEmpIdByName(name)) // Mapper를 호출하여 emp_id 조회
                    .filter(Objects::nonNull) // Null 값 제거
                    .collect(Collectors.toList());
        }

        public List<String> findEmpNamesByIds(List<String> approverIds) {
            // ID 리스트가 비어 있는지 확인
            if (approverIds == null || approverIds.isEmpty()) {
                return Collections.emptyList();
            }

            // emp_id를 기반으로 이름 리스트를 생성
            return approverIds.stream()
                    .map(id -> employeeMapper.findNameByEmpId(id)) // Mapper를 호출하여 이름 조회
                    .filter(Objects::nonNull) // Null 값 제거
                    .collect(Collectors.toList());

        }

        public String findEmpNameById(String empId) {
            return employeeMapper.findNameByEmpId(empId);
        }

        public FileDTO getFileByPmId(String pmId) {
            return approvalMapper.findFilesByPmId(pmId);
        }





        // 승인 처리
        public void approve(String pmId, String empId) {
            // 1. 현재 승인자를 '승인' 상태로 업데이트
            approvalMapper.updateApprovalStatus(pmId, empId, "승인");



            // 2. 모든 대기 중인 승인자의 상태를 '진행 중'으로 업데이트
            approvalMapper.updateAllPendingToInProgress(pmId);


            // 3. 모든 승인자가 완료되었는지 확인
            boolean allApproved = approvalMapper.allApprovalsCompleted(pmId);
            if (allApproved) {
                approvalMapper.updateDocumentStatus(pmId, "완료");
            } else {
                System.out.println("아직 승인자가 남아 있습니다.");
            }
        }



        public void reject(String pmId, String empId, String reason) {
            Map<String, Object> params = new HashMap<>();
            params.put("pmId", pmId);
            params.put("status", "반려");

            // 1. 해당 승인자의 상태를 반려로 변경
            approvalMapper.updateApprovalStatusWithReason(pmId, empId, "반려", reason);

            // 2. 이전 승인자들의 상태도 '반려'로 변경
            approvalMapper.updatePreviousApproversToRejected(pmId, empId);

            // 3. 문서 전체 상태를 '반려'로 업데이트
            approvalMapper.updateAllApprovalStatusesToRejected(params);
        }

        // 전결 처리
        public void finalize(String pmId, String empId) {
            approvalMapper.updateApprovalAllPass(pmId, empId, "전결");
//            approvalMapper.updateDocumentStatus(pmId, "완료"); // 전결 시 바로 완료 처리
        }



        // 현재 사용자의 승인 순서 확인
        public int getCurrentApprovalOrder(String pmId, String empId) {
            Integer order = approvalMapper.getApprovalOrder(pmId, empId);
            return (order != null) ? order : -1;
        }

        // 현재 사용자가 승인 순서인지 확인
        public boolean isCurrentApprover(String pmId, String currentEmpId) {
            Integer currentOrder = approvalMapper.getApprovalOrder(pmId, currentEmpId);
            Integer requiredOrder = approvalMapper.getCurrentApprovalStep(pmId);
            if (requiredOrder == null) {
                requiredOrder = -1; // 기본값으로 처리
            }

            if (requiredOrder == null) {

                return false; // 또는 예외 처리
            }


            return currentOrder != null && currentOrder.equals(requiredOrder);
        }

        // 완료된 문서 조회
        public List<DocumentDTO> getCompletedDocuments(String empId, int limit, int offset) {
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);
            return approvalMapper.getCompletedDocuments(params);
        }

        public int getCompletedDocumentsCount(String empId) {
            return approvalMapper.getCompletedDocumentsCount(empId);
        }

        public List<DocumentDTO> getInProgressDocuments(String empId, int limit, int offset) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 매퍼에 전달할 파라미터 준비
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);




            // 진행 중 문서 조회
            List<DocumentDTO> inProgressDocs = approvalMapper.findInProgressDocuments(params);




            return inProgressDocs;
        }


        public int getInProgressDocumentsCount(String empId) {
            return approvalMapper.countInProgressDocuments(empId);
        }

        public List<DocumentDTO> getRejectedDocuments(String empId, int limit, int offset) {
            if (empId == null || empId.isEmpty()) {
                throw new IllegalArgumentException("empId는 null 또는 비어있을 수 없습니다.");
            }

            // 매퍼에 전달할 파라미터 준비
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);



            // 반려 문서 조회
            List<DocumentDTO> rejectedDocs = approvalMapper.findRejectedDocuments(params);



            return rejectedDocs;
        }

        public int getRejectedDocumentsCount(String empId) {
            return approvalMapper.countRejectedDocuments(empId);
        }

        // 승인자들의 상태 확인 (추가된 메서드)
        public boolean isAllApproversApproved(String pmId) {
            List<String> approverStatuses = approvalMapper.getApproversStatus(pmId);
            return approverStatuses != null && approverStatuses.stream().allMatch(status -> "승인".equals(status));
        }

        // 문서 상태를 완료로 업데이트
        public void updateStatusToCompleted(String pmId) {

            approvalMapper.updateDocumentStatus(pmId, "완료");
        }



        public void updateLeaveType(String pmId, String leaveType) {
            approvalMapper.updateLeaveType(pmId, leaveType);
        }

        public ApprovalDetailDTO getApprovalDetail(String pmId) throws Exception {
            // DAO 호출하여 데이터베이스에서 상세 정보 조회
            ApprovalDetailDTO approvalDetail = approvalMapper.findApprovalDetailById(pmId);

            if (approvalDetail == null) {
                // 데이터가 없을 경우 예외 처리
                throw new Exception("해당 문서를 찾을 수 없습니다.");
            }

            return approvalDetail;
        }

        public void updateLeaveDate(String pmId, LocalDate leaveDate) {
            approvalMapper.updateLeaveDate(pmId, leaveDate);
        }

        public String getLeaveTypeForDocument(String pmId) {
            return approvalMapper.findLeaveTypeByPmId(pmId);
        }

        public LocalDate getLeaveDateForDocument(String pmId) {
            return approvalMapper.findLeaveDateByPmId(pmId);
        }

        // 승인자가 한 명인지 확인
        public boolean isSingleApprover(String pmId) {
            List<String> approvers = approvalMapper.getApprovers(pmId);
            return approvers.size() == 1;
        }

        public boolean isFirstApprover(String pmId, String empId) {
            String firstApprover = approvalMapper.findFirstApprover(pmId);
            return firstApprover != null && firstApprover.equals(empId);
        }

        public boolean isFinalApprover(String pmId, String currentEmpId) {
            Integer approverOrderdozang = approvalMapper.getApprovalOrderdozang(pmId, currentEmpId); // 현재 승인자의 순서
            Integer maxOrder = approvalMapper.getMaxApprovalOrder(pmId); // 최대 승인 순서


            return approverOrderdozang != null && approverOrderdozang.equals(maxOrder);
        }

        public void updateEmployeeLeave(String empId, BigDecimal deduction, String leaveType) {


            // 업데이트 직전 사용자 정보 확인
            LoginUserDTO beforeUser = userMapper.findUserById(empId);


            userMapper.updateUserLeave(empId, deduction, leaveType);

            // 업데이트 직후 사용자 정보 확인
            LoginUserDTO afterUser = userMapper.findUserById(empId);
           
        }
        public String getRejectReasonByApprover(String pmId, String currentEmpId) {
            return approvalMapper.getRejectReasonByApprover(pmId, currentEmpId);
        }

            @Transactional
            public void insertAttendanceRecord(AttendanceDTO attendanceDTO) {
                // 이미 해당 날짜에 근태 기록이 있는지 확인
                int existingRecords = approvalMapper.checkAttendanceExists(
                        attendanceDTO.getEmpId(),
                        attendanceDTO.getAttDate()
                );

                // 기존 기록이 없을 때만 삽입
                if (existingRecords == 0) {
                    approvalMapper.insertAttendance(attendanceDTO);
                }
            }


        public int countInProgressDocuments(String empId) {
            return approvalMapper.countInProgressDocuments(empId);
        }

        public int countRejectedDocuments(String empId) {
            return approvalMapper.countRejectedDocuments(empId);
        }

        public List<DocumentDTO> getApprovableDocuments(String empId, int limit, int offset) {
            Map<String, Object> params = new HashMap<>();
            params.put("empId", empId);
            params.put("limit", limit);
            params.put("offset", offset);

            return approvalMapper.findApprovableDocuments(params);
        }
    }




