package com.ohgiraffers.refactorial.auth.dao;

import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EmailConfirmMapper {

    UserDTO findUserById(String confirmEmpId);

}
