package com.ohgiraffers.refactorial.booking.model.dao;

import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CabinetMapper {

    List<CabinetDTO> getAllCabinets();

    CabinetDTO getCabinetInfo(String roomNo);
}
