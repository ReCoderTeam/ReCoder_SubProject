package com.ohgiraffers.refactorial.admin.controller;

import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Component
public class Extract {

    public static final String SRC = "ex_springboot.pdf";
//    public static final String IMG = "ci_logo.jpg";
    public static final String DESC = "springboot.pdf";

    @Bean
    public Boolean extractPDF(List<TktReserveDTO> dataList){
        System.out.println("dataList = " + dataList);
        
        for (TktReserveDTO data : dataList){
            System.out.println("data = " + data);
        }

        Context context = new Context();
        context.setVariable("dataList",dataList);

        if (dataList != null){
            return true;
        }
        return false;
    }
    
}
