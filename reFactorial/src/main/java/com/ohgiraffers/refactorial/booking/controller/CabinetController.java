package com.ohgiraffers.refactorial.booking.controller;

import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import com.ohgiraffers.refactorial.booking.service.CabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/booking/*")
public class CabinetController {

    @Autowired
    private CabinetService cabinetService;

    // 회의실을 선택하여 예약 폼을 띄우는 메서드
    @GetMapping("/bookingForm")
    public CabinetDTO showBookingForm(@RequestParam("roomNo") String roomNo, Model model) {

        System.out.println("roomNo = " + roomNo);

        CabinetDTO cabinet = cabinetService.getCabinetInfo(roomNo);

        System.out.println("cabinet = " + cabinet);

        model.addAttribute("cabinet",cabinet);

        return cabinet;
    }

}
