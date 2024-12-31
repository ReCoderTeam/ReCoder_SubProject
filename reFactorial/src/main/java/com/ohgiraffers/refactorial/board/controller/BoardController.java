package com.ohgiraffers.refactorial.board.controller;

import com.ohgiraffers.refactorial.board.model.dto.*;
import com.ohgiraffers.refactorial.board.service.BoardService;
import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import com.ohgiraffers.refactorial.fileUploade.model.service.UploadFileService;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final UploadFileService uploadService;

    @Autowired
    public BoardController(BoardService boardService, UploadFileService uploadService) {
        this.boardService = boardService;
        this.uploadService = uploadService;
    }

    // 게시물 전체조회
    @GetMapping("list") // url로 이동
    public String list(@RequestParam int categoryCode, Model model, HttpSession session,
                       @RequestParam(value = "page", defaultValue = "1") int currentPage,
                       @RequestParam(required = false) String searchContents) {

//        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");   // 로그인한 유저 정보를 가져옴

        // 페이지네이션
        int limit = 15; // 한 페이지당 문서 수
        int totalBoardList = boardService.getBoardListCount(categoryCode); // 전체 게시글 개수 가져오기
        int totalPages = totalBoardList > 0 ? (int) Math.ceil((double) totalBoardList / limit) : 1; // 총 페이지 수

        // 현재 페이지 범위 검증
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit; // offset 계산

        // 게시물 목록 가져오기
        List<BoardDTO> postList = boardService.postList(categoryCode, limit, offset, searchContents);

        // 문서 번호 설정
        for (int i = 0; i < postList.size(); i++) {
            postList.get(i).setRowNum(totalBoardList - offset - i);
        }

        // 이전/다음 페이지 설정
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;

        // 모델에 데이터 추가
        model.addAttribute("currentPage", currentPage); // 현재 페이지 번호
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수
        model.addAttribute("prevPage", prevPage); // 이전 페이지 번호
        model.addAttribute("nextPage", nextPage); // 다음 페이지 번호
        model.addAttribute("totalBoardList", totalBoardList); // 전체 페이지 갯수

        model.addAttribute("postList", postList);    // 템플릿에 값 전달
        model.addAttribute("categoryCode", categoryCode);   // 카테고리코드를 게시물 등록페이지로 이동시키기 위한 셋팅
        model.addAttribute("currentCategory", categoryCode);    // 게시판 사이드바에 값 전달

        return "board/list";   // html 페이지로 이동
    }

    // 게시물 등록 / 수정 페이지로 이동
    @GetMapping("freeBoardRegist")
    public String freeBoardRegist(@RequestParam int categoryCode,
                                  @RequestParam(required = false) String postId,
                                  Model model,
                                  HttpSession session) {

        List<VoteItemDTO> voteItemList = boardService.itemView(postId);     // 항목리스트
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");
        String viewAuth = String.valueOf(user.getViewAuth());

        model.addAttribute("categoryCode", categoryCode);
        model.addAttribute("currentCategory", categoryCode);    // 게시판 사이드바에 값 전달
        model.addAttribute("voteItemList", voteItemList);
        model.addAttribute("viewAuth",viewAuth);

        //postId가 있으면 "수정" / 없으면 "등록"
        if (postId != null) {
            // postId로 조회
            BoardDTO postDetail = boardService.postDetail(postId);              // postDetail(postID(PK))로 DTO가져옴
            model.addAttribute("postDetail", postDetail);           // 상세페이지 전달
        }

        return "board/freeBoardRegist";
    }


    // 게시물 등록 / 수정
    @PostMapping("freeBoardRegist")
    public String boardPost(@RequestParam String title,
                            @RequestParam String content,
                            @RequestParam int categoryCode,
                            @RequestParam(required = false) String option1,
                            @RequestParam(required = false) String option2,
                            @RequestParam(required = false) String option3,
                            @RequestParam(required = false) String option4,
                            @RequestParam(required = false) String option5,
                            @RequestParam(required = false) List<MultipartFile> postFileList,
                            @RequestParam(required = false) String postId,
                            Model model, HttpSession session) throws IOException {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        String boardId = "BO" + String.format("%05d", (int) (Math.random() * 100000));     // 5자리 랜덤 문자열 생성(게시물 ID)

        // 게시물의 정보
        BoardDTO board = new BoardDTO();        // BoardDTO 객체에 밑에있는 값을 담음
        if (!postId.equals("")) {                   // postId가 없으면 boardId 생성 (등록)
            board.setPostId(postId);           // postId가 있으면 postId 전달 (수정)
        } else {
            board.setPostId(boardId);
        }
        board.setPostTitle(title);              // 게시물 제목
        board.setPostContent(content);          // 게시물 내용
        board.setPostCreationDate(LocalDateTime.now()); // 게시물 등록 시간
        board.setEmpId(user.getEmpId());        // 작성자 사원번호
        board.setPostModificationDate(LocalDateTime.now()); // 게시물 수정 시간
        board.setCategoryCode(categoryCode);    // 게시물 카테고리 코드


        // 카테고리가 4(투표게시판)일 때
        if (categoryCode == 4) {

            List<String> options = new ArrayList<>();

            if (option1 != null && !option1.isEmpty()) {
                options.add(option1);
            }
            if (option2 != null && !option2.isEmpty()) {
                options.add(option2);
            }
            if (option3 != null && !option3.isEmpty()) {
                options.add(option3);
            }
            if (option4 != null && !option4.isEmpty()) {
                options.add(option4);
            }
            if (option5 != null && !option5.isEmpty()) {
                options.add(option5);
            }

            // postId에 해당하는 투표 항목 목록 삭제
            boardService.deleteVoteItemList(postId);

            // 반복문돌려서 1개씩 만들기 (게시물 ID, 항목 이름)
            for (String option : options) {
                VoteItemDTO optionDTO = new VoteItemDTO();
                if (!postId.equals("")) {                   // postId가 없으면 boardId 생성 (등록)
                    optionDTO.setPostId(postId);           // postId가 있으면 postId 전달 (수정)
                } else {
                    optionDTO.setPostId(boardId);
                }
                optionDTO.setItemTitle(option);   // 항목 이름
//                System.out.println("optionDTO의 값: " + optionDTO);

                boardService.optionResult(optionDTO);   // 투표 항목 DTO로 전달
            }



        }

        if (categoryCode == 3){



            if (!postFileList.isEmpty()) {
                board.setPostfile(postFileList);
                board.setAttachment(1);

                uploadService.upLoadFile(postFileList,boardId);
            }
        }

        boardService.post(board);   // 게시물 등록 기능

        model.addAttribute("postId", postId);

        return "redirect:/board/list?categoryCode=" + categoryCode; // 내 API를 호출
    }

    // 게시물 상세페이지 / 투표 항목
    @GetMapping("postDetail")
    public String postDetail(@RequestParam String postId,
                             @RequestParam int categoryCode,
                             Model model, HttpSession session) {

        // 로그인한 유저 정보를 가져옴
        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 게시물 상세 내용
        BoardDTO postDetail = boardService.postDetail(postId);
        // 댓글 리스트 가져옴
        List<CommentDTO> commentList = boardService.commentView(postId);

        // 좋아요 카운트
        for (int i = 0; i < commentList.size(); i++) {

            CommentDTO commentDTO = commentList.get(i);
            commentDTO.setLoginUserEmpId(user.getEmpId());
            int commentLikesCount = boardService.commentLikesCount(commentDTO);

            // DTO 에서 좋아요 갯수를 카운트함
            commentDTO.setLikeCount(commentLikesCount);

            // 본인 투표여부
            int isMyLike = boardService.isMyLike(commentDTO);

            // 만약 좋아요를 눌렀으면 true, 아니면 false
            if (isMyLike > 0) {
                commentDTO.setMyLike(true);
            } else {
                commentDTO.setMyLike(false);
            }

        }

        // 항목 리스트 가져옴
        List<VoteItemDTO> voteItemList = boardService.itemView(postId);     // 수정 시 항목 데이터를 불러옴

        // 투표한사람이면 보여주는 항목리스트
        List<VoteResultDTO> resultInquiryList = boardService.voteComplete(postId, user.getEmpId());
        // 투표 TotalList
        List<VoteTotalDTO> voteTotalList = boardService.getVoteResults(postId);


        // 초기 투표 수 0으로 세팅
        voteItemList.stream().forEach(item->{
            item.setTotalCount(0);
        });


        // ItemId가 일치할 경우 총 투표 수 세팅
        for(int i=0; i<voteItemList.size(); i++){
            for(int j=0; j<voteTotalList.size(); j++){
                if(voteItemList.get(i).getItemId() == voteTotalList.get(j).getItemId()){
                    voteItemList.get(i).setTotalCount(voteTotalList.get(j).getVoteCount());
                }
            }
        }

        // 본인이 투표한 사람일 경우 체크
        for(int i=0; i<resultInquiryList.size(); i++){
            for(int j=0; j<voteItemList.size(); j++){
                if(resultInquiryList.get(i).getEmpId().equals(user.getEmpId()) && resultInquiryList.get(i).getItemId() == voteItemList.get(j).getItemId()){
                    voteItemList.get(j).setOwnVoted(true);
                    // 로그인한 사용자의 투표 여부
                    model.addAttribute("votedByPost", true);
                    //
                }
            }
        }

        // 첨부파일
        if (categoryCode == 3){
            if (postDetail.getAttachment() == 1){
                List<UploadFileDTO> uploadFileList = uploadService.findFileByMappingId(postId);

                if (!uploadFileList.isEmpty()){
                    model.addAttribute("attachmentFileList",uploadFileList);
                }
            }
        }

        model.addAttribute("postDetail", postDetail);   // 상세페이지
        model.addAttribute("commentView", commentList);     // 댓글 조회
        model.addAttribute("user", user);   // user정보 postDetail에 전달(게시물 수정,삭제 권한)
        model.addAttribute("voteView", voteItemList);   // 항목 리스트
        model.addAttribute("currentCategory", categoryCode);    // 게시판 사이드바에 값 전달

        return "board/postDetail";
    }

    // 투표 결과를 DB에 저장
    @PostMapping("vote")
    public String voteResult(@RequestParam(required = false) List<Integer> voteIdList,
                             @RequestParam int categoryCode,
                             @RequestParam String postId,
                             HttpSession session,
                             RedirectAttributes rttr) {


        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴
        List<VoteResultDTO> voteItemList = new ArrayList<>();   // 항목 1개씩 담음

        // 반복문을 돌려 항목을 리스트에 저장
        if (voteIdList != null) {

            for (int i = 0; i < voteIdList.size(); i++) {
                voteItemList.add(new VoteResultDTO(null, user.getEmpId(), voteIdList.get(i), postId));
            }

        }

        // 항목을 선택하지 않고 투표했을 시 새로고침
        if (voteItemList.size() > 0) {
            boardService.voteResult(voteItemList);
        }

//        List<VoteResultDTO> voteSelectResult = boardService.getVoteResults(postId);    // 투표결과 List 형태로 가져옴

//        System.out.println("voteSelectResult = " + voteSelectResult);


        // 결과 데이터를 모델에 추가하여 HTML(화면) 전달  @@@ 리다이렉트 플래시 사용 @@@
//        rttr.addFlashAttribute("voteResults", voteSelectResult);

        return "redirect:/board/postDetail?postId=" + postId + "&categoryCode=" + categoryCode;    // redirect 를 사용하는 이유 - postDetail에 있는 데이터 사용을 위해!
    }

    // 게시물 삭제
    @GetMapping("postDelete")
    public String deletePost() {
        return "board/postDelete";
    }

    @PostMapping("postDelete")
    public String postDelete(@RequestParam String postId, @RequestParam int categoryCode) {

        // 삭제 기능
        boardService.postDelete(postId);

        return "redirect:/board/list?categoryCode=" + categoryCode;
    }

    // 댓글 등록
    @PostMapping("comment")
    public String comment(@RequestParam String comment,
                          @RequestParam String postId,
                          @RequestParam int categoryCode,
                          HttpSession session, Model model) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        CommentDTO commentDetail = new CommentDTO();

        LocalDateTime commentTime = LocalDateTime.now();

        commentDetail.setCommentContent(comment);
        commentDetail.setPostId(postId);
        commentDetail.setEmpId(user.getEmpId());
        commentDetail.setCommentCreationDate(commentTime);

        model.addAttribute("comment", comment);

        boardService.comment(commentDetail);

        return "redirect:/board/postDetail?categoryCode=" + categoryCode + "&postId=" + postId;
    }

    // 댓글 삭제
    @GetMapping("commentDelete")
    @ResponseBody   // 화면 이동이 아닌 데이터만 넘겨주기 위해 사용
    public List<CommentDTO> commentDelete(@RequestParam int commentId, @RequestParam String postId, HttpSession session) {

        // 댓글을 삭제하는 기능
        boardService.commentDelete(commentId);

        // 댓글 리스트 가져옴
        List<CommentDTO> commentList = boardService.commentView(postId);

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        // 좋아요 카운트
        for (int i = 0; i < commentList.size(); i++) {

            CommentDTO commentDTO = commentList.get(i);
            commentDTO.setLoginUserEmpId(user.getEmpId());
            int commentLikesCount = boardService.commentLikesCount(commentDTO);

            // DTO 에서 좋아요 갯수를 카운트함
            commentDTO.setLikeCount(commentLikesCount);

            // 본인 투표여부
            int isMyLike = boardService.isMyLike(commentDTO);

            // 만약 좋아요를 눌렀으면 true, 아니면 false
            if (isMyLike > 0) {
                commentDTO.setMyLike(true);
            } else {
                commentDTO.setMyLike(false);
            }

        }

        // 화면에 댓글 리스트 형태로 가져옴
        return commentList;
    }

    //  좋아요 조회
    @PostMapping("commentLikes")
    public String commentLikes(@RequestParam String postId,
                               @RequestParam int categoryCode,
                               HttpSession session,
                               Model model) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴


        boardService.commentLikes(postId);

        model.addAttribute("like", postId);


        return "redirect:/board/postDetail?categoryCode=" + categoryCode + "&postId=" + postId;
    }

    // 좋아요 등록
    @PostMapping("commentLikesInsert")
    public String commentLikesInsert(@RequestParam int commentId,
                                     @RequestParam String postId,
                                     @RequestParam int categoryCode,
                                     @RequestParam boolean isMyLike,
                                     HttpSession session) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");     // 로그인한 유저의 정보를 가져옴

        String userEmpId = user.getEmpId();

        CommentLikesDTO commentLikes = new CommentLikesDTO();

        commentLikes.setCommentId(commentId);
        commentLikes.setEmpId(userEmpId);

        // 좋아요 했을 때 isMyLike가 트루면 등록 아니면 삭제
        if (isMyLike) {
            boardService.commentLikesDelete(commentLikes);
        } else {
            boardService.commentLikesInsert(commentLikes);
        }


        return "redirect:/board/postDetail?categoryCode=" + categoryCode + "&postId=" + postId;
    }


}
