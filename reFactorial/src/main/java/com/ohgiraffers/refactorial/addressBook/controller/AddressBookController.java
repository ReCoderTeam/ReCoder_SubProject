package com.ohgiraffers.refactorial.addressBook.controller;

import com.ohgiraffers.refactorial.addressBook.model.dto.FactoryDTO;
import com.ohgiraffers.refactorial.addressBook.service.AddressBookService;
import com.ohgiraffers.refactorial.approval.model.dto.EmployeeDTO;
import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/addressBook")
public class AddressBookController {

    private final AddressBookService addressBookService;
    private final UserMapper userMapper;


    @Autowired
    public AddressBookController(AddressBookService addressBookService,UserMapper userMapper){
        this.addressBookService = addressBookService;
        this.userMapper = userMapper;
    }


    // 사내 직원 검색
    @ResponseBody
    @GetMapping("/employees")
    public List<EmployeeDTO> getAllEmployees() {
        return addressBookService.getAllEmployees();
    }

    @ResponseBody
    @GetMapping("/search")
    public List<EmployeeDTO> searchEmployees(@RequestParam("keyword") String keyword) {

        return addressBookService.searchEmployees(keyword);
    }

    @GetMapping("/employeeAddressBook")
    public String getAddressBookPage(){

        return "addressBook/addressBookMain";
    }


    //협력업체 검색
    @ResponseBody
    @GetMapping("/factory")
    public List<FactoryDTO> getAllFactories(){
        return addressBookService.getAllFactories();
    }

    @ResponseBody
    @GetMapping("/searchFactory")
    public List<FactoryDTO> searchFactories(@RequestParam("keyword") String keyword){
        System.out.println("검색 키워드: " + keyword); // 디버깅 로그 추가
        return addressBookService.searchFactories(keyword);
    }

    @GetMapping("/factoryAddressBook")
    public String factoryAddressBook(){
        return "addressBook/factory";
    }




}
