package com.ohgiraffers.refactorial.approval.model.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface ApprovalMapper {
    void insertPm(String pmId, String title, String category, Date date, String attachment);

    void insertApprover(String approver, String pmId, boolean b);

    void insertReferrer(String pmId, String referrer);
}