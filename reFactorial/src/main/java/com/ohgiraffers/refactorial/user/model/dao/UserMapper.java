package com.ohgiraffers.refactorial.user.model.dao;

import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserDTO findByUsername(String username);

    int addEmployee(UserDTO userDTO);
}
