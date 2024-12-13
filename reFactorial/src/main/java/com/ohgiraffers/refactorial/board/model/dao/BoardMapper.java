package com.ohgiraffers.refactorial.board.model.dao;

import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 게시글 전체조회
    List<BoardDTO> postList(int categoryCode);

    // 게시글 등록
    void boardPost(BoardDTO board);

    // 상세페이지
    BoardDTO postDetail(int postId);

    // 게시글 수정
    void updatePost(BoardDTO board);

    // 게시글 삭제
    void postDelete(int postId);

    // 댓글 등록
    void comment(CommentDTO comment);
}
