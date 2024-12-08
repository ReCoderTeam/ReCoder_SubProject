package com.ohgiraffers.refactorial.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

    @GetMapping("user/freeBoard")
    public String freeBoard() {
        return "board/freeBoard";
    }

    @GetMapping("user/document")
    public String document() {
        return "board/document";
    }

    @GetMapping("user/vote")
    public String vote() {
        return "board/vote";
    }

    @GetMapping("user/event")
    public String event() {
        return "board/event";
    }

    @GetMapping("user/freeBoardRegist")
    public String freeBoardRegist() {
        return "board/freeBoardRegist";
    }

}
