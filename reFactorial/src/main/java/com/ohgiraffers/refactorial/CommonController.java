package com.ohgiraffers.refactorial;

import com.ohgiraffers.refactorial.auth.model.AuthDetails;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;

@ControllerAdvice
public class CommonController {

    @ModelAttribute("LoginUserInfo")
    public LoginUserDTO LoginUserInfo(HttpSession session) {

        LoginUserDTO user = (LoginUserDTO) session.getAttribute("LoginUserInfo");

        if (user == null) {
            return new LoginUserDTO();
        }

        return user;
    }
}
