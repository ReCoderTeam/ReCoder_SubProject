package com.ohgiraffers.refactorial.board.model.dao;

import com.ohgiraffers.refactorial.board.model.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 게시글 전체조회
    List<BoardDTO> postList(int categoryCode, int limit, int offset, String searchContents);

    // 게시글 등록
    void boardPost(BoardDTO board);

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

    // 투표 항목 전달
    void optionResult(VoteItemDTO options);

    // 투표 항목 조회
    List<VoteItemDTO> itemView(String postId);

    // 투표 결과 생성
    void voteResult(List<VoteResultDTO> voteItemList);

    // 투표 선택 결과 조회
    List<VoteTotalDTO> getVoteResults(String postId);

    // 투표 결과조회 (사용자)
    List<VoteResultDTO> voteComplete(String postId, String empId);

    // 투표 통계
    List<VoteItemDTO> getTotalVote(String postId);

    // 게시글 카테고리별 카운트
    int getBoardListCount(int categoryCode);
}
