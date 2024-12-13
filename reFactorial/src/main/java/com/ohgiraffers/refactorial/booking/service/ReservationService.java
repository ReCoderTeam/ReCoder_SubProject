package com.ohgiraffers.refactorial.booking.service;

import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import com.ohgiraffers.refactorial.booking.model.dto.ReservationDTO;
import com.ohgiraffers.refactorial.booking.model.dao.ReservationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationDAO reservationDAO;

    @Autowired
    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    // 모든 예약 목록을 가져오는 메서드 추가
    public List<ReservationDTO> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    // 회의실 예약을 처리하는 메서드
    public void reserveConferenceRoom(ReservationDTO reservationDTO) {
        reservationDAO.insertReservation(reservationDTO);
    }

    // 예약 삭제 메서드
    public void deleteReservationById(String reservationId) {
        reservationDAO.deleteReservationById(reservationId);
    }
    // 중복 방지 메서드
    public boolean isReservationAvailable(BigDecimal conferenceRoomNo, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 예약이 겹치는지 체크하는 로직
        List<ReservationDTO> conflicts = reservationDAO.checkReservationConflict(conferenceRoomNo, date, startTime, endTime);
        return conflicts.isEmpty();
    }

    // 개인페이지 예약 내역 메서드
    public List<ReservationDTO> getUserReservations(String empId) {
        return reservationDAO.getUserReservations(empId);
    }
}
