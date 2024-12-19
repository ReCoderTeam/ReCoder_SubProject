package com.ohgiraffers.refactorial.admin.model.service;

import com.ohgiraffers.refactorial.admin.model.dao.AdminMapper;
import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper am;

    @Autowired
    private PasswordEncoder encoder;

    public List<LoginUserDTO> getAllEmployee(int sendDept, String sendSearchEmpName) {
        Map<String, Object> searchData = new HashMap<>();

        searchData.put("sendDept",sendDept);
        searchData.put("sendSearchEmpName",sendSearchEmpName);

        return am.getAllEmployee(searchData);
    }

    public Integer modifyEmpInfoUpdate(UserDTO userDTO) {

        if (userDTO.getEmpPwd() != null){
            userDTO.setEmpPwd(encoder.encode(userDTO.getEmpPwd()));
        }

        System.out.println("userDTO = " + userDTO);

        return am.modifyEmpInfoUpdate(userDTO);
    }

    public List<AttendanceDTO> getByDateAtt(String selectedDay, int offset, int size, String searchDept, String searchEmpName) {

        Map<String, Object> sendData = new HashMap<>();
        sendData.put("selectedDay",selectedDay);
        sendData.put("offset",offset);
        sendData.put("size",size);
        sendData.put("searchDept",searchDept);
        sendData.put("searchEmpName",searchEmpName);

        return am.getByDateAtt(sendData);
    }

    public int getTotalCountByDateAtt(String selectedDay, String searchDept, String searchEmpName) {

        Map<String, Object> sendData = new HashMap<>();
        sendData.put("selectedDay",selectedDay);
        sendData.put("searchDept",searchDept);
        sendData.put("searchEmpName",searchEmpName);

        return am.getTotalCountByDateAtt(sendData);
    }

    public Integer modifyEmpAtt(String empId, String attDate, String selectedStatus) {

        Map<String, Object> sendData = new HashMap<>();

        sendData.put("empId",empId);
        sendData.put("attDate",attDate);
        sendData.put("selectedStatus",selectedStatus);

        return am.modifyEmpAtt(sendData);
    }

    public List<TktReserveDTO> getTktReserve(String selectedDay) {
        return am.getTktReserve(selectedDay);
    }
}
