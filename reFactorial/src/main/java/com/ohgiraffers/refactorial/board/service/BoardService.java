package com.ohgiraffers.refactorial.board.service;

import com.ohgiraffers.refactorial.board.model.dao.BoardMapper;
import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final BoardMapper boardMapper;

    @Autowired
    public BoardService(BoardMapper boardMapper) {

        this.boardMapper = boardMapper;
    }

//    // 게시글 전체조회(공지)
//    public List<BoardDTO> notiPostList() {
//
//        return boardMapper.notiPostList();
//    }

    // 게시글 전체조회(자유)
    public List<BoardDTO> postList(int categoryCode) {

        return boardMapper.postList(categoryCode);
    }

    // 게시글 등록
    public void post(BoardDTO board) {

        boardMapper.boardPost(board);
    }

    // 상세페이지
    public BoardDTO postDetail(int postId) {

        return boardMapper.postDetail(postId);
    }

    // 게시글 수정
    @Transactional
    public void updatePost(BoardDTO board) {

        boardMapper.updatePost(board);
    }

    // 게시글 삭제
    @Transactional
    public void postDelete(int postId) {

        boardMapper.postDelete(postId);
    }

    // 댓글 등록
    public void comment(CommentDTO comment) {

        boardMapper.comment(comment);
    }
}
