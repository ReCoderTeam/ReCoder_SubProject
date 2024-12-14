package com.ohgiraffers.refactorial.booking.controller;

import com.ohgiraffers.refactorial.booking.model.dto.ReservationDTO;
import com.ohgiraffers.refactorial.booking.service.ReservationService;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // 회의실을 선택하여 예약 폼을 띄우는 메서드
    @GetMapping("/bookingForm")
    public String showBookingForm(@RequestParam("roomNo") String roomNo, Model model) {
        // Ensure roomNo is passed correctly to the booking form page
        model.addAttribute("roomNo", roomNo);
        return "/booking/bookingForm";
    }

    @GetMapping("/booking/bookingList")
    public String showBookingList(HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");
        List<ReservationDTO> userReservations = reservationService.getUserReservations(user.getEmpId());
        model.addAttribute("userReservations", userReservations);
        return "/booking/bookingList"; // 수정된 부분
    }

    // 예약을 처리하는 메서드
    @PostMapping("/reserve")
    public String makeReservation(@ModelAttribute ReservationDTO reservationDTO, Model model) {
        reservationDTO.setReservationId(UUID.randomUUID().toString());

        // 오늘 날짜 이전 예약 불가 체크
        if (reservationDTO.getReservationDate().isBefore(LocalDate.now())) {
            model.addAttribute("errorMessage", "예약은 오늘 이후의 날짜에만 가능합니다.");
            return "booking/bookingForm"; // 예약 폼으로 돌아가기
        }

        // 시작 시간과 종료 시간을 비교하여 유효성 검사 추가
        if (reservationDTO.getReservationStartTime().isAfter(reservationDTO.getReservationEndTime())) {
            model.addAttribute("errorMessage", "예약 시작 시간이 종료 시간보다 늦을 수 없습니다.");
            return "booking/bookingForm"; // 시작 시간이 종료 시간보다 늦을 경우 예약 폼으로 돌아가기
        }

        // 예약 가능 여부 확인
        boolean isAvailable = reservationService.isReservationAvailable(
                reservationDTO.getConferenceRoomNo(),
                reservationDTO.getReservationDate(),
                reservationDTO.getReservationStartTime(),
                reservationDTO.getReservationEndTime()
        );

        if (!isAvailable) {
            model.addAttribute("errorMessage", "예약 시간이 중복됩니다. 다른 시간을 선택해 주세요.");
            // 중복되면 showBookingForm 으로 리디렉션
            return "redirect:/bookingForm?roomNo=" + reservationDTO.getConferenceRoomNo(); // 리디렉션 경로 수정
        }

        try {
            reservationService.reserveConferenceRoom(reservationDTO);
            return "redirect:/user/booking";  // 예약 후, 예약 목록 페이지로 리다이렉트
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "booking/bookingForm";  // 에러 발생 시 예약 폼으로 돌아가기
        }
    }

    // 예약 삭제 메서드
    @PostMapping("/deleteReservation")
    public String deleteReservationById(@RequestParam("reservationId") String reservationId) {
        try {
            reservationService.deleteReservationById(reservationId);
            return "redirect:/booking/bookingList"; // 삭제 후, 예약 목록 페이지로 리다이렉트
        } catch (RuntimeException e) {
            return "redirect:/booking/bookingList";  // 삭제 중 오류 발생 시 에러 페이지로 이동
        }
    }

    @GetMapping("/api/reservations")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations); // 정상적으로 예약 목록 반환
    }
}
