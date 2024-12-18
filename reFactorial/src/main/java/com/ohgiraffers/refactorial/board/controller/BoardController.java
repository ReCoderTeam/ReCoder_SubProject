package com.ohgiraffers.refactorial.board.controller;

import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import com.ohgiraffers.refactorial.board.model.dto.*;
import com.ohgiraffers.refactorial.board.service.BoardService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시물 전체조회
    @GetMapping("list") // url로 이동
    public String list(@RequestParam int categoryCode, Model model, HttpSession session) {

        List<BoardDTO> postList = boardService.postList(categoryCode);      // 전체조회 기능

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");   // 로그인한 유저 정보를 가져옴

        model.addAttribute("postList", postList);    // 템플릿에 값 전달
        model.addAttribute("categoryCode", categoryCode);   // 카테고리코드를 게시물 등록페이지로 이동시키기 위한 셋팅
        model.addAttribute("currentCategory", categoryCode);    // 게시판 사이드바에 값 전달

        return "/board/list";   // html 페이지로 이동

    }

    // 게시물 등록 페이지로 이동
    @GetMapping("freeBoardRegist")
    public String freeBoardRegist(@RequestParam int categoryCode, Model model) {

        model.addAttribute("categoryCode", categoryCode);

        return "/board/freeBoardRegist";
    }


    // 게시물 등록
    @PostMapping("freeBoardRegist")
    public String boardPost(@RequestParam String title,
                            @RequestParam String content,
                            @RequestParam int categoryCode,
//                            @RequestParam List<String> item,
                            Model model, HttpSession session) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴
        String boardId = "BO" + String.format("%05d", (int) (Math.random() * 100000));     // 5자리 랜덤 문자열 생성(게시물 ID)

        // 게시물의 정보
        BoardDTO board = new BoardDTO();        // BoardDTO 객체에 밑에있는 값을 담음
        board.setPostId(boardId);               // 게시물 번호
        board.setPostTitle(title);              // 게시물 제목
        board.setPostContent(content);          // 게시물 내용
        board.setPostCreationDate(LocalDateTime.now()); // 게시물 등록 시간
        board.setEmpId(user.getEmpId());        // 작성자 사원번호
        board.setPostModificationDate(LocalDateTime.now()); // 게시물 수정 시간
        board.setCategoryCode(categoryCode);    // 게시물 카테고리 코드

//        // VoteItemDTO 리스트 생성 및 설정
//        List<VoteItemDTO> voteItems = new ArrayList<>();
//        for (String voteItem : item) {
//            VoteItemDTO voteItemDTO = new VoteItemDTO();
//            voteItemDTO.setPostId(boardId);
//            voteItemDTO.setItemTitle(voteItem);
//            voteItems.add(voteItemDTO);
//        }

//        System.out.println("voteItems" + voteItems);
//
//
//        board.setVoteItems(voteItems); // BoardDTO에 투표 항목 설정


        boardService.post(board);   // 게시물 등록 기능
//        boardService.saveVoteItems(voteItems); // 투표 항목 등록


        return "redirect:/board/list?categoryCode=" + categoryCode; // 내 API를 호출
    }

    // 게시물 상세페이지 / 댓글 등록
    @GetMapping("postDetail")
    public String postDetail(@RequestParam String postId, Model model, HttpSession session) {

        // 로그인한 유저 정보를 가져옴
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        // 게시물 상세
        BoardDTO postDetail = boardService.postDetail(postId);
        // 댓글 리스트 가져옴
        List<CommentDTO> comment = boardService.commentView(postId);

        model.addAttribute("postDetail", postDetail);
        model.addAttribute("commentView", comment);
        model.addAttribute("user", user);   // user정보 postDetail에 전달(게시물 수정,삭제 권한)

        return "/board/postDetail";
    }

    // 게시물 삭제
    @GetMapping("postDelete")
    public String deletePost() {
        return "/board/postDelete";
    }

    @PostMapping("postDelete")
    public String postDelete(@RequestParam String postId, @RequestParam int categoryCode) {

        // 삭제 기능
        boardService.postDelete(postId);

        return "redirect:/board/list?categoryCode=" + categoryCode;
    }

    // 게시물 수정
    @GetMapping("postUpdate")
    public String postUpdate(@RequestParam String postId, Model model) {

        BoardDTO postDetail = boardService.postDetail(postId);

        model.addAttribute("modify", postDetail);

        return "/board/postUpdate";
    }

    @PostMapping("postUpdate")
    public String updatePost(@ModelAttribute BoardDTO board) {

        board.setPostModificationDate(LocalDateTime.now()); // 게시물 수정 시간
        boardService.updatePost(board);



//        return "redirect:/board/postDetail?postId=" + board.getPostId();    // 상세페이지 머무르기
        return "redirect:/board/list?categoryCode=" + board.getCategoryCode();  // 게시판 이동

    }

    // 댓글 등록
    @PostMapping("comment")
    public String comment(@RequestParam String comment, @RequestParam String postId, HttpSession session, Model model) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        CommentDTO commentDetail = new CommentDTO();

        LocalDateTime commentTime = LocalDateTime.now();

        commentDetail.setCommentContent(comment);
        commentDetail.setPostId(postId);
        commentDetail.setEmpId(user.getEmpId());
        commentDetail.setCommentCreationDate(commentTime);

        model.addAttribute("comment", comment);

        boardService.comment(commentDetail);

        return "redirect:/board/postDetail?postId=" + postId;
    }

    @GetMapping("commentDelete")
    @ResponseBody   // 화면 이동이 아닌 데이터만 넘겨주기 위해 사용
    public List<CommentDTO> commentDelete(@RequestParam int commentId, @RequestParam String postId) {

        // 댓글을 삭제하는 기능
        boardService.commentDelete(commentId);

        // 화면에 댓글 리스트 형태로 가져옴
        return boardService.commentView(postId);

    }

}
