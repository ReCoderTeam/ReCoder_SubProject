package com.ohgiraffers.refactorial.booking.service;

import com.ohgiraffers.refactorial.booking.model.dao.CabinetMapper;
import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CabinetService {

    private final CabinetMapper cabinetMapper;

    @Autowired
    public CabinetService(CabinetMapper cabinetMapper) {
        this.cabinetMapper = cabinetMapper;
    }

    // 모든 회의실 목록을 가져오는 메서드 추가
    public List<CabinetDTO> getAllCabinets() {
        return cabinetMapper.getAllCabinets();
    }

    public CabinetDTO getCabinetInfo(String roomNo) {
        return cabinetMapper.getCabinetInfo(roomNo);
    }
}
