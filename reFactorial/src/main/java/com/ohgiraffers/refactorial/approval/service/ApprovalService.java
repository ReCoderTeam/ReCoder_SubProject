    package com.ohgiraffers.refactorial.approval.service;

    import com.ohgiraffers.refactorial.approval.model.dao.ApprovalMapper;
    import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
    import com.ohgiraffers.refactorial.approval.model.dto.*;
    import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.ui.Model;


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


        public String saveApproval(ApprovalRequestDTO approvalRequestDTO, String creatorId) {

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

            approvalMapper.insertPm(params); // 매퍼 호출
            return String.valueOf(params.get("pmId")); // 생성된 pmId 반환
        }


        public void saveApprovers(String pmId, List<Map<String, Object>> approvers) {
            if (approvers != null && !approvers.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("pmId", pmId);
                params.put("approvers", approvers);
                approvalMapper.saveApprovers(params);
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

        // 파일 추가
        public void saveFile(FileDTO file) {
            approvalMapper.insertFile(file);
        }

        // 파일 삭제
        public void deleteFileById(int fileId) {
            approvalMapper.deleteFileByFileId(fileId);
        }

        // PM ID로 파일 다운로드 정보 제공
        public FileDTO getFileById(int fileId) {
            return approvalMapper.findFileByFileId(fileId);
        }

        public FileDTO getFileByFileName(String fileName) {
            return approvalMapper.findFileByFileName(fileName);
        }


        // 승인 처리
        public void approve(String pmId, String empId) {
            approvalMapper.updateApprovalStatus(pmId, empId, "승인");
            checkAndUpdateDocumentStatus(pmId); // 모든 승인 완료 시 상태 업데이트

        }

        // 반려 처리
        public void reject(String pmId, String empId, String reason) {
            approvalMapper.updateApprovalStatusWithReason(pmId, empId, "반려", reason);
            approvalMapper.updateDocumentStatus(pmId, "반려"); // 반려 상태 업데이트
        }

        // 전결 처리
        public void finalize(String pmId, String empId) {
            approvalMapper.updateApprovalStatus(pmId, empId, "전결");
            approvalMapper.updateDocumentStatus(pmId, "완료"); // 전결 시 바로 완료 처리
        }

        // 모든 승인 상태 확인 후 최종 상태 업데이트
        private void checkAndUpdateDocumentStatus(String pmId) {
            try {
                List<String> statuses = approvalMapper.getAllApprovalStatuses(pmId);

                if (statuses == null || statuses.isEmpty()) {

                    return; // 상태가 없으면 아무 작업도 하지 않음
                }

                boolean allApproved = statuses.stream().allMatch(status -> "승인".equals(status));
                System.out.println("All approvers approved for pmId " + pmId + ": " + allApproved);

                if (allApproved) {
                    approvalMapper.updateDocumentStatus(pmId, "완료");
                    System.out.println("Document status updated to 완료 for pmId " + pmId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in checkAndUpdateDocumentStatus for pmId " + pmId);
            }


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

        // 진행 중 문서 조회
        public List<DocumentDTO> getInProgressDocuments(String empId, int limit, int offset) {
            return approvalMapper.findInProgressDocuments(empId, limit, offset);
        }

        public int getInProgressDocumentsCount(String empId) {
            return approvalMapper.countInProgressDocuments(empId);
        }

        // 반려된 문서 조회
        public List<DocumentDTO> getRejectedDocuments(String empId, int limit, int offset) {
            return approvalMapper.findRejectedDocuments(empId, limit, offset);
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



        public void updateRemainingApproversToInProgress(String pmId, String currentEmpId) {
            approvalMapper.updateRemainingApproversToInProgress(pmId, currentEmpId);
            System.out.println("pmId: " + pmId);
            System.out.println("currentEmpId: " + currentEmpId);
        }
    }




