package com.ohgiraffers.refactorial;

import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import com.ohgiraffers.refactorial.booking.model.dto.ReservationDTO;
import com.ohgiraffers.refactorial.booking.service.CabinetService;
import com.ohgiraffers.refactorial.booking.service.ReservationService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private CabinetService cabinetService;


    @GetMapping("/")
    public String defaultURL(Model model){
        return "auth/login";
    }

    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/user")
    public String mainControll(Model model){
        return "index";
    }

    
    @GetMapping("/booking")
    public String showReservations(HttpSession session , Model model) {

        // 회의실 목록 가져오기
        List<CabinetDTO> allCabinets = cabinetService.getAllCabinets();

        model.addAttribute("cabinets",allCabinets);

        return "/booking/booking";
    }

    @GetMapping("/user/inquiry")
    public String inquiryPage() {
        return "/inquiry/inquiry";
    }

    @GetMapping("/user/approvalMain")
    public String approvalMainController(){
        return "/approvals/approvalMain";
    }

    @GetMapping("/user/notification")
    public String notification() {
        return "/board/notification";
    }

    @GetMapping("/user/sharedWork")
    public String sharedWork(){
        return "/board/sharedWork";
    }
  
    @GetMapping("/auth/login")
    public void loginPage(){};

    @GetMapping("/user/addressBookMain")
    public String addressBookPage(){
        return "/addressbook/addressBookMain";
    }

    @GetMapping("/admin/main")
    public String adminPage(){
        return "admin/admin_main";
    };

    @GetMapping("user/myPage")
    public String myPage(HttpSession session, Model model){

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        String deptName = memberService.findDeptName(user.getDeptCode());
        String positionName = memberService.findPositionName(user.getPositionValue());

        model.addAttribute("deptName",deptName);
        model.addAttribute("positionName",positionName);

        return "myPage/myPage";
    }

    @GetMapping("/user/mail")
    public String mailPage(){
        return "/mail/mailMain";
    }

    @GetMapping("/user/product")
    public String zasaPage(){
        return "zasaPage/product";
    }

}

