package com.ohgiraffers.refactorial;

import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.admin.model.service.AdminService;
import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import com.ohgiraffers.refactorial.attendance.service.AttendanceService;
import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import com.ohgiraffers.refactorial.board.service.BoardService;
import com.ohgiraffers.refactorial.booking.model.dto.CabinetDTO;
import com.ohgiraffers.refactorial.booking.service.CabinetService;
import com.ohgiraffers.refactorial.booking.service.ReservationService;
import com.ohgiraffers.refactorial.inquiry.model.dto.InquiryDTO;
import com.ohgiraffers.refactorial.inquiry.service.AdminInquiryService;
import com.ohgiraffers.refactorial.inquiry.service.InquiryService;
import com.ohgiraffers.refactorial.mail.model.dto.MailDTO;
import com.ohgiraffers.refactorial.mail.service.MailService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class MainController {

    private final MemberService memberService;
    private final CabinetService cabinetService;
    private final MailService mailService;
    private final AttendanceService attendanceService;
    private final BoardService boardService;
    private final InquiryService inquiryService;
    private final AdminService as;
    private final AdminInquiryService adminInquiryService;

    @Autowired
    private ApprovalService approvalService;


    @Autowired
    public MainController(MemberService memberService,
                          CabinetService cabinetService,
                          MailService mailService,
                          AttendanceService attendanceService,
                          BoardService boardService,
                          InquiryService inquiryService,
                          AdminService as,
                          AdminInquiryService adminInquiryService
                        ){
        this.memberService = memberService;
        this.cabinetService = cabinetService;
        this.mailService = mailService;
        this.attendanceService = attendanceService;
        this.boardService = boardService;
        this.inquiryService = inquiryService;
        this.as = as;
        this.adminInquiryService = adminInquiryService;
    }


    @GetMapping("/")
    public String defaultURL(Model model){
        return "auth/login";
    }

    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/user")
    public String mainControll(Model model,HttpSession session){
        // 공지사항 게시물 가져오기
        List<BoardDTO> boardList = boardService.postList(1, 10, 0, null);

        if (!boardList.isEmpty()){
            model.addAttribute("boardList",boardList);
        }


        // 투표 게시물 가져오기
        List<BoardDTO> votePostList = boardService.postList(4, 10, 0, null);

        if (!votePostList.isEmpty()){
            // 최근꺼 3개만 가져오기
            List<BoardDTO> recently3List = new ArrayList<>();

            for (int i = 0; i <votePostList.size() ;i++){
                recently3List.add(votePostList.get(i));
            }

            model.addAttribute("recently3List",recently3List);
        }

        // 내가 받은 메일 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String empId = loginUser.getEmpId(); // 보낸 사람의 empId로 설정

        List<MailDTO> receivedMails = mailService.getReceivedMails(empId);




        if(!receivedMails.isEmpty()){
            List<Map<String, Object>> mailWithSenderList = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (MailDTO mail : receivedMails){
                String name = memberService.getNameById(mail.getSenderEmpId());

                LocalDateTime sentDate = mail.getSentDate();
                String date = sentDate.format(formatter);

                Map<String, Object> mailWithDate = new HashMap<>();
                mailWithDate.put("mail", mail);  // 기존 mail 객체 저장
                mailWithDate.put("senderName", name); // 보낸 사람 이름
                mailWithDate.put("date", date);  // 날짜 정보 추가

                mailWithSenderList.add(mailWithDate);
            }

            model.addAttribute("receivedMails", mailWithSenderList);
        }

        return "index";
    }

    @GetMapping("/booking")
    public String showReservations(HttpSession session , Model model) {

        // 회의실 목록 가져오기
        List<CabinetDTO> allCabinets = cabinetService.getAllCabinets();

        model.addAttribute("cabinets",allCabinets);

        return "booking/booking";
    }

    @GetMapping("/user/inquiry")
    public String showInquiryPageForUser(Model model) {
        model.addAttribute("currentPage", "inquiry"); // 1:1 문의하기 페이지
        return "inquiry/sendInquiry";
    }

    @PostMapping("/inquiry/sendInquiry")
    public String sendInquiry(@ModelAttribute InquiryDTO inquiryDTO,
                              @RequestParam("inquiryFiles") List<MultipartFile> inquiryFileList,
                              HttpSession session,
                              Model model) throws IOException {
        // 로그인 유저 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 유저의 empId를 InquiryDTO 에 설정
        String senderEmpId = loginUser.getEmpId();
        inquiryDTO.setEmpId(senderEmpId);

        Set<String> generatedIds = new HashSet<>();
        String IQRid;
        do {
            IQRid = "IQR" + String.format("%05d", (int) (Math.random() * 100000));
        } while (!generatedIds.add(IQRid));

        inquiryDTO.setIqrValue(IQRid);

        try {
            // 메일 전송과 수신자 정보 저장
            inquiryService.sendInquiry(inquiryDTO, inquiryFileList);
        } catch (Exception e) {
            model.addAttribute("error", "메일 전송 중 오류가 발생했습니다.");
            return "inquiry/sendInquiry";
        }

        return "redirect:/inquiry/inquiryList";  // 폼 제출 후 다시 같은 페이지로 리다이렉트
    }


    @GetMapping("/user/approvalMain")
    public String approvalMainController(Model model,HttpSession session){

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        String empId = user.getEmpId();

        // 각 카테고리별 문서 수 조회
        int waitingCount = approvalService.getWaitingCount(empId);
        int inProgressCount = approvalService.getInProgressDocumentsCount(empId);
        int completedCount = approvalService.getCompletedDocumentsCount(empId);
        int rejectedCount = approvalService.getRejectedDocumentsCount(empId);

        // 각 탭에 표시할 최근 문서들 조회 (예: 최근 5개)
        List<DocumentDTO> draftDocuments = approvalService.getMyDocuments(empId, 3, 0);

        List<DocumentDTO> approveDocuments = approvalService.getApprovableDocuments(empId, 3, 0);

        List<DocumentDTO> referenceDocuments = approvalService.getReferenceDocuments(empId, 3, 0);

        // 모델에 데이터 추가
        model.addAttribute("waitingCount", waitingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        model.addAttribute("draftDocuments", draftDocuments);
        model.addAttribute("approveDocuments", approveDocuments);
        model.addAttribute("referenceDocuments", referenceDocuments);

        return "approvals/approvalMain";
    }

    @GetMapping("/user/notification")
    public String notification() {
        return "board/notification";
    }

    @GetMapping("/user/allWork")
    public String sharedWork(){
        return "sharedWork/allWork";
    }
  
    @GetMapping("/auth/login")
    public void loginPage(){};

    @GetMapping("/admin/main")
    public String adminPage(Model model){

        // 입사 현황
        Map<String, Object> empHiredDateChart = memberService.getHiredDateGroupBy();
        
        // 오늘 출근 현황
        LocalDate today = LocalDate.now();
        Map<String,Object> attendanceChart = attendanceService.getAttendanceGroupBy(today);

        // 최근 이벤트 게시물
        List<BoardDTO> eventList = boardService.postList(5, 10, 0, null);

        if (eventList != null && !eventList.isEmpty()){
            model.addAttribute("recentlyEvent",eventList.get(0));
            String postId = eventList.get(0).getPostId();
            List<CommentDTO> commentList = boardService.commentView(postId);
            model.addAttribute("commentList",commentList);
        }

        List<InquiryDTO> inquiryList = adminInquiryService.getAllInquiries();


        model.addAttribute("attendanceChart",attendanceChart);
        model.addAttribute("empHiredDateChart",empHiredDateChart);
        model.addAttribute("inquiryList",inquiryList);

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
    public String mailPage(Model model , HttpSession session){
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String senderEmpId = loginUser.getEmpId(); // 보낸 사람의 empId로 설정
        String receiverEmpId = loginUser.getEmpId(); // 받은 사람의 empId로 설정

        // 내가 보낸 메일만 가져오기
        List<MailDTO> sentMails = mailService.getSentMails(senderEmpId);
        model.addAttribute("sentMails", sentMails);

        // 내가 받은 메일만 가져오기, 내가 보낸 메일은 제외
        List<MailDTO> receivedMails = mailService.getReceivedMails(receiverEmpId);
        // 자신에게 보낸 메일을 제외한 받은 메일만 모델에 추가
        receivedMails.removeIf(mail -> mail.getSenderEmpId().equals(receiverEmpId));
        model.addAttribute("receivedMails", receivedMails);

        return "mail/mailMain"; // 전체 메일 페이지로 리턴
    }

    @GetMapping("/goldTicket")
    public String goldTicket(Model model){
        
        String selectedDay = null;

        List<TktReserveDTO> result = as.getTktReserve(selectedDay);

        int totalCount = as.getTotalCountTktReserve();

        model.addAttribute("goldTicket",result);
        model.addAttribute("totalCount",totalCount);

        return "goldTicket/goldTicket";
    }
}
