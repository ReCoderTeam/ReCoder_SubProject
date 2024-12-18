package com.ohgiraffers.refactorial.board.model.dao;

import com.ohgiraffers.refactorial.board.model.dto.BoardDTO;
import com.ohgiraffers.refactorial.board.model.dto.CommentDTO;
import com.ohgiraffers.refactorial.board.model.dto.VoteItemDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    // 게시글 전체조회
    List<BoardDTO> postList(int categoryCode);

    // 게시글 등록
    void boardPost(BoardDTO board);

    // 투표 게시글 등록
    void insertVoteItem(VoteItemDTO item);

    // 상세페이지
    BoardDTO postDetail(String postId);

    // 게시글 수정
    void updatePost(BoardDTO board);

    // 게시글 삭제
    void postDelete(String postId);

    // 댓글 등록
    void comment(CommentDTO comment);

    // 댓글 조회
    List<CommentDTO> commentView (String postId);

    // 댓글 삭제
    void commentDelete(int commentId);

//    // 투표게시글 등록
//    void votePost(VoteDTO voteBoard);

    // 투표 게시물 조회
//    List<BoardDTO> voteList(int categoryCode);

}
