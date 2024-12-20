package com.ohgiraffers.refactorial.approval.model.dao;

import com.ohgiraffers.refactorial.approval.model.dto.ApprovalDetailDTO;
import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.model.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApprovalMapper {
    void insertPm(Map<String, Object> params);

    

    int getWaitingCount(String empId);

    void saveApprovers(Map<String, Object> params);
    void saveReferrers(Map<String, Object> params);


    List<DocumentDTO> getReferenceDocuments(Map<String, Object> params);

    int getTotalReferenceDocuments(@Param("empId") String empId);


    List<DocumentDTO> getMyDocuments(Map<String, Object> params);

    int getMyDocumentsCount(String empId);


    void insertApprovalFile(Map<String, Object> params);

    DocumentDTO getDocumentById(String pmId);



    // 파일
    FileDTO findFilesByPmId(String pmId);

    void insertFile(FileDTO file);

    void deleteFileByFileId(int fileId);

    FileDTO findFileByFileId(int fileId);

    FileDTO findFileByFileName(String fileName);

    List<DocumentDTO> getWaitingDocuments(Map<String, Object> params);

   




    List<String> getAllApprovalStatuses(@Param("pmId") String pmId);

    Integer getApprovalOrder(@Param("pmId") String pmId,
                             @Param("empId") String empId);

    List<DocumentDTO> getCompletedDocuments(Map<String, Object> params);

    int getCompletedDocumentsCount(String empId);

   

    int countInProgressDocuments(String empId);

    List<DocumentDTO> findRejectedDocuments(String empId, int limit, int offset);

    int countRejectedDocuments(String empId);

    List<String> getApproversStatus(@Param("pmId") String pmId);

    void updateApprovalStatus(@Param("pmId") String pmId,
                              @Param("empId") String empId,
                              @Param("status") String status);


    boolean allApprovalsCompleted(@Param("pmId") String pmId);

    String findLeaveTypeByPmId(String pmId);
    LocalDate findLeaveDateByPmId(String pmId);


    void updateLeaveType(String pmId, String leaveType);

    ApprovalDetailDTO findApprovalDetailById(String pmId);

    void updateLeaveDate(String pmId, LocalDate leaveDate);


    Integer getCurrentApprovalStep(String pmId);

    List<String> getApprovers(String pmId);

    void updateDocumentStatus(@Param("pmId") String pmId, @Param("status") String status);




    String findFirstApprover(String pmId);



    List<DocumentDTO> findInProgressDocuments(Map<String, Object> params);

    void insertApprover(Map<String, Object> params);



    void updateAllPendingToInProgress(String pmId);

    void updateApprovalAllPass(String pmId, String empId, String 전결);


    void updateApprovalStatusWithReason(@Param("pmId") String pmId,
                                        @Param("empId") String empId,
                                        @Param("status") String status,
                                        @Param("reason") String reason);

    void updateAllApprovalStatusesToRejected(String pmId, String 반려);
}
