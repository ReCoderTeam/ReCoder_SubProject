package com.ohgiraffers.refactorial.board.service;

import com.ohgiraffers.refactorial.approval.model.dto.DocumentDTO;
import com.ohgiraffers.refactorial.board.model.dao.BoardMapper;
import com.ohgiraffers.refactorial.board.model.dto.*;
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

    // 게시글 카테고리별 목록 조회
    public List<BoardDTO> postList(int categoryCode, int limit, int offset, String searchContents) {

        return boardMapper.postList(categoryCode, limit, offset, searchContents);
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

    // 특정 게시글에 해당하는 투표 항목 목록 삭제
    public void deleteVoteItemList(String postId) {

        boardMapper.deleteVoteItemList(postId);
    }
    public List<VoteItemDTO> itemView(String postId) {

        return boardMapper.itemView(postId);
    }

    // 투표 결과 생성
    public void voteResult(List<VoteResultDTO> voteItemList) {

        boardMapper.voteResult(voteItemList);
    }

    // 투표 선택 결과 조회(전체)
    public List<VoteTotalDTO> getVoteResults(String postId) {

        return boardMapper.getVoteResults(postId);
    }

    // 투표 선택 결과 조회(사용자 한정)
    public List<VoteResultDTO> voteComplete(String postId, String empId) {
        return boardMapper.voteComplete(postId, empId);
    }

    // 게시물 카테고리별 전체 카운트
    public int getBoardListCount(int categoryCode) {

        return boardMapper.getBoardListCount(categoryCode);
    }



    // 댓글 좋아요 조회
    public void commentLikes(String postId) {

        boardMapper.commentLikes(postId);
    }


    public void commentLikesInsert(CommentLikesDTO commentLikes) {

        boardMapper.commentLikesInsert(commentLikes);
    }

    public int commentLikesCount(CommentDTO commentDTO) {
        return boardMapper.commentLikesCount(commentDTO);
    }

    public int isMyLike(CommentDTO commentDTO) {

        return boardMapper.isMyLike(commentDTO);

    }

    // 좋아요 삭제
    public void commentLikesDelete(CommentLikesDTO commentLikes) {
        boardMapper.commentLikesDelete(commentLikes);
    }

    public int getBoardListCounts(int categoryCode, String searchContents) {
        Map<String, Object> params = new HashMap<>();
        params.put("categoryCode", categoryCode);
        params.put("searchContents", searchContents);
        return boardMapper.getBoardListCounts(params);
    }
}
