package com.ohgiraffers.refactorial.board.service;

import com.ohgiraffers.refactorial.board.model.dao.BoardMapper;
import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import com.ohgiraffers.refactorial.board.model.dto.VoteItemDTO;
import com.ohgiraffers.refactorial.board.model.dto.VoteResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    private final BoardMapper boardMapper;

    @Autowired
    public BoardService(BoardMapper boardMapper) {

        this.boardMapper = boardMapper;
    }

    // 게시글 전체조회
    public List<BoardDTO> postList(int categoryCode) {

        return boardMapper.postList(categoryCode);
    }

    // 게시글 등록
    public void post(BoardDTO board) {

        boardMapper.boardPost(board);
    }

    // 상세페이지
    public BoardDTO postDetail(String postId) {

        return boardMapper.postDetail(postId);
    }

    // 게시글 수정
    @Transactional
    public void updatePost(BoardDTO board) {

        boardMapper.updatePost(board);
    }

    // 게시글 삭제
    @Transactional
    public void postDelete(String postId) {

        boardMapper.postDelete(postId);
    }

    // 댓글 등록
    public void comment(CommentDTO comment) {

        boardMapper.comment(comment);
    }

    // 댓글 조회
    public List<CommentDTO> commentView(String comment) {

        return boardMapper.commentView(comment);
    }

    // 댓글 삭제
    public void commentDelete(int commentId) {
        boardMapper.commentDelete(commentId);
    }

    // 투표 항목 생성
    public void optionResult(VoteItemDTO options) {

        boardMapper.optionResult(options);
    }

    public List<VoteItemDTO> itemView(String postId) {

        return boardMapper.itemView(postId);
    }

    // 투표 결과 생성
    public void voteResult(List<VoteResultDTO> voteItemList) {

        boardMapper.voteResult(voteItemList);
    }

//    // 투표 결과 DB전달
//    public List<VoteResultDTO> voteResultView(String voteResult) {
//
//        return boardMapper.voteResultView(voteResult);
//    }
}
