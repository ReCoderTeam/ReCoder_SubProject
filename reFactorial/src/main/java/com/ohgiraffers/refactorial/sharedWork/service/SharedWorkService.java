package com.ohgiraffers.refactorial.sharedWork.service;

import com.ohgiraffers.refactorial.sharedWork.model.dao.SharedWorkMapper;
import com.ohgiraffers.refactorial.sharedWork.model.dto.SharedWorkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedWorkService {

    private final SharedWorkMapper sharedWorkMapper;

    @Autowired
    public SharedWorkService(SharedWorkMapper sharedWorkMapper) {
        this.sharedWorkMapper = sharedWorkMapper;
    }

    // 업무 전체 조회
    public List<SharedWorkDTO> getAllSharedWork(int deptCode) {
        return sharedWorkMapper.getAllSharedWork(deptCode);
    }


    // 업무 저장
    public void saveSharedWork(SharedWorkDTO sharedWork) {

        sharedWorkMapper.saveSharedWork(sharedWork);
    }

    // 삭제
    public SharedWorkDTO deleteEvent(String workId) {
        // 삭제 전 데이터 조회
        SharedWorkDTO eventToDelete = sharedWorkMapper.findEventById(workId);

        if (eventToDelete == null) {
            throw new IllegalStateException("삭제할 이벤트가 존재하지 않습니다.");
        }

        // 데이터 삭제 수행
        int deletedRows = sharedWorkMapper.deleteEventById(workId);

        if (deletedRows == 0) {
            throw new IllegalStateException("삭제 작업에 실패했습니다.");
        }

        // 삭제된 데이터 반환
        return eventToDelete;
    }

    // 수정
    public boolean updateSharedWork(SharedWorkDTO updatedWork) {

        return sharedWorkMapper.updateSharedWork(updatedWork) > 0;
    }
}
