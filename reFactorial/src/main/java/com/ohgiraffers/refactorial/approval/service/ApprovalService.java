package com.ohgiraffers.refactorial.approval.service;

import com.ohgiraffers.refactorial.approval.model.dao.ApprovalMapper;
import com.ohgiraffers.refactorial.approval.model.dao.EmployeeMapper;
import com.ohgiraffers.refactorial.approval.model.dto.ApprovalRequestDTO;
import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ApprovalService {


    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private EmployeeMapper employeeMapper;


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

        approvalMapper.insertPm(params); // 매퍼 호출
        return String.valueOf(params.get("pmId")); // 생성된 pmId 반환
    }


    public void saveApprovers(String pmId, List<String> approvers) {
        // 중복 제거
        List<String> uniqueApprovers = new ArrayList<>(new HashSet<>(approvers));

        if (!uniqueApprovers.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("pmId", pmId);
            params.put("approvers", uniqueApprovers);

            approvalMapper.saveApprovers(params);
            }
    }
        public void saveReferrers (String pmId, List < String > referrers){
            for (String referrer : referrers) {
                approvalMapper.insertReferrer(pmId, referrer);
            }

        }

        public List<DocumentDTO> getWaitingDocuments () {
            return approvalMapper.getWaitingDocuments();
        }


    }





