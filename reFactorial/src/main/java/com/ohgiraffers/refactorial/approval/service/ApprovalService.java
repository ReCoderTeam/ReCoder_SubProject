package com.ohgiraffers.refactorial.approval.service;

import com.ohgiraffers.refactorial.approval.model.dao.ApprovalMapper;
import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
import com.ohgiraffers.refactorial.approval.model.dto.ApprovalRequestDTO;
import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import com.ohgiraffers.refactorial.approval.model.dto.FileDTO;
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
        String pmId = "PM" + String.format("%03d", (int) (Math.random() * 1000));


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


    public void approve(String pmId, String empId) {
        approvalMapper.updateApprovalStatus(pmId, empId, "승인");
        checkAndUpdateDocumentStatus(pmId); // 승인 상태 확인 후 문서 상태 업데이트
    }

    public void reject(String pmId, String empId, String reason) {
        approvalMapper.updateApprovalStatusWithReason(pmId, empId, "반려", reason); // 반려 상태 업데이트 및 사유 저장
        approvalMapper.updateDocumentStatus(pmId, "반려");
    }

    public void finalize(String pmId, String empId) {
        approvalMapper.updateApprovalStatus(pmId, empId, "전결");
        approvalMapper.updateDocumentStatus(pmId, "완료"); // 전결 시 바로 문서 완료 처리
    }

    // 모든 승인 상태 확인 후 최종 상태 업데이트
    private void checkAndUpdateDocumentStatus(String pmId) {
        List<String> statuses = approvalMapper.getAllApprovalStatuses(pmId);

        if (statuses == null || statuses.isEmpty()) {
            return; // 승인 상태가 없으면 바로 반환
        }

        // "반려" 상태가 하나라도 있으면 반려 처리
        if (statuses.stream().filter(Objects::nonNull).anyMatch(status -> status.equals("반려"))) {
            approvalMapper.updateDocumentStatus(pmId, "반려");
            return;
        }

        // NULL이나 빈 상태를 제외하고 "승인" 또는 "전결" 상태만 확인
        long validStatuses = statuses.stream()
                .filter(Objects::nonNull)
                .filter(status -> !status.isEmpty())
                .count();

        boolean allApproved = statuses.stream()
                .filter(Objects::nonNull)
                .filter(status -> !status.isEmpty())
                .allMatch(status -> status.equals("승인") || status.equals("전결"));

        // 모든 상태가 승인 또는 전결이거나, 유효한 상태가 하나만 존재할 때 처리
        if (validStatuses > 0 && allApproved) {
            approvalMapper.updateDocumentStatus(pmId, "완료");
        }
    }

    // 현재 사용자의 승인 순서 확인
    public int getCurrentApprovalOrder(String pmId, String empId) {
        Integer order = approvalMapper.getApprovalOrder(pmId, empId);
        return (order != null) ? order : -1; // 순서가 없으면 -1 반환
    }

    // 현재 사용자가 승인자 여부 확인
    public boolean isCurrentApprover(String pmId, String empId) {
        Integer order = approvalMapper.getApprovalOrder(pmId, empId);
        return order != null && order > 0;
    }

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

    // 진행 중 문서 가져오기
    public List<DocumentDTO> getInProgressDocuments(String empId, int limit, int offset) {
        return approvalMapper.findInProgressDocuments(empId, limit, offset);
    }

    // 진행 중 문서 개수 가져오기
    public int getInProgressDocumentsCount(String empId) {
        return approvalMapper.countInProgressDocuments(empId);
    }

    // 반려된 문서 가져오기
    public List<DocumentDTO> getRejectedDocuments(String empId, int limit, int offset) {
        return approvalMapper.findRejectedDocuments(empId, limit, offset);
    }

    // 반려된 문서 개수 가져오기
    public int getRejectedDocumentsCount(String empId) {
        return approvalMapper.countRejectedDocuments(empId);
    }




}





//
//    public List<String> findEmpNamesByIds(List<String> empIds) {
//        return empIds.stream()
//                .map(id -> employeeMapper.findNameByEmpId(id))  // emp_id에 해당하는 이름 조회
//                .collect(Collectors.toList());
//    }



