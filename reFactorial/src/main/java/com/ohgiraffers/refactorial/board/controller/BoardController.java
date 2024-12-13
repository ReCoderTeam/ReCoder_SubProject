package com.ohgiraffers.refactorial.board.controller;

import com.ohgiraffers.refactorial.approval.service.ApprovalService;
import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import com.ohgiraffers.refactorial.board.service.BoardService;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @GetMapping("list")
    public String list(@RequestParam int categoryCode, Model model) {

            List<BoardDTO> postList = boardService.postList(categoryCode);

            System.out.println("postList = " + postList);

            model.addAttribute("postList", postList);    // 템플릿에 값 전달

            model.addAttribute("categoryCode", categoryCode);   // 카테고리코드를 게시물 등록페이지로 이동시키기 위한 셋팅

//        System.out.println("postList = " + postList);   // 값이 잘 들어오는지 확인

        return "/board/list";

    }

    // 게시물 등록 페이지로 이동
    @GetMapping("freeBoardRegist")
    public String freeBoardRegist(@RequestParam int categoryCode, Model model) {

        model.addAttribute("categoryCode", categoryCode);

        return "/board/freeBoardRegist";
    }


    // 게시물 등록
    @PostMapping("freeBoardRegist")
    public String boardPost(@RequestParam String title, @RequestParam String content, @RequestParam int categoryCode,
                            Model model, HttpSession session) {

        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        BoardDTO board = new BoardDTO();        // BoardDTO 객체에 밑에있는 값을 담음
        board.setPostId(board.getPostId());     // 게시물 번호
        board.setPostTitle(title);              // 게시물 제목
        board.setPostContent(content);          // 게시물 내용
        board.setPostCreationDate(LocalDateTime.now()); // 게시물 등록 시간
        board.setEmpId(user.getEmpId());        // 작성자 사원번호
        board.setPostModificationDate(LocalDateTime.now()); // 게시물 수정 시간
        board.setCategoryCode(categoryCode);        // 게시물 카테고리 코드

//        System.out.println("글쓴이 = " + user);

        boardService.post(board);

        return "redirect:/board/list?categoryCode=" + categoryCode;
    }

    // 게시물 상세페이지
    @GetMapping("postDetail")
    public String postDetail(@RequestParam int postId, Model model) {

        BoardDTO postDetail = boardService.postDetail(postId);

//        System.out.println("postDetail = " + postDetail);

        model.addAttribute("postDetail", postDetail);

        return "/board/postDetail";
    }

    // 게시물 삭제
    @GetMapping("postDelete")
    public String deletePost() {
        return "/board/postDelete";
    }

    @PostMapping("postDelete")
    public String postDelete(@RequestParam int postId, @RequestParam int categoryCode) {

        boardService.postDelete(postId);

        return "redirect:/board/list?categoryCode=" + categoryCode;
    }

    // 게시물 수정
    @GetMapping("postUpdate")
    public String postUpdate(@RequestParam int postId, Model model) {

        BoardDTO postDetail = boardService.postDetail(postId);

//        System.out.println("postDetail = " + postDetail);

        model.addAttribute("modify", postDetail);

        return "/board/postUpdate";
    }

    @PostMapping("postUpdate")
    public String updatePost(@ModelAttribute BoardDTO board) {

        board.setPostModificationDate(LocalDateTime.now()); // 게시물 수정 시간
        boardService.updatePost(board);

//        return "redirect:/board/postDetail?postId=" + board.getPostId();    // 상세페이지 머무르기
        return "redirect:/board/list?categoryCode="+board.getCategoryCode();  // 게시판 이동

    }

    // 댓글 등록
    @PostMapping("comment")
    public String comment(@RequestParam String comment, @RequestParam int postId, HttpSession session) {

        UserDTO user = (UserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        CommentDTO commentDetail = new CommentDTO();

        System.out.println("comment = " + comment);
        System.out.println("postId = " + postId);

        LocalDate commentTime = LocalDate.now();

        System.out.println("commentTime = " + commentTime);

        commentDetail.setCommentContent(comment);
        commentDetail.setPostId(postId);
        commentDetail.setEmpId(user.getEmpId());
        commentDetail.setCommentCreationDate(commentTime);

        boardService.comment(commentDetail);


        return "redirect:/board/postDetail?postId="+postId;
    }

    @GetMapping("document")
    public String document() {
        return "/board/document";
    }

    @GetMapping("vote")
    public String vote() {
        return "/board/vote";
    }

    @GetMapping("event")
    public String event() {
        return "/board/event";
    }

}
