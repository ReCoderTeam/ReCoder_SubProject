package com.ohgiraffers.refactorial.user.model.service;

import com.ohgiraffers.refactorial.user.model.dao.UserMapper;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public int addEmployee(UserDTO userDTO) {
        // DB에 insert 하기 전 service 계층에서 값을 가공(암호화)해야 한다.
        userDTO.setEmpPwd(encoder.encode(userDTO.getEmpPwd()));

        int result = userMapper.addEmployee(userDTO);

        return result;
    }

    // 사용자가 입력한 ID를 입력받아 회원을 조회하는 메소드
    public LoginUserDTO findUserId(String username) {

//        LoginUserDTO user = userMapper.findByUsername(username);

        return userMapper.findByUsername(username);
    }

    public String findDeptName(int deptCode) {

        String deptName = userMapper.findDeptName(deptCode);

        return deptName;
    }

    public String findPositionName(int positionValue) {
        String positionName = userMapper.findPositionName(positionValue);

        return positionName;
    }

    public boolean pwMatch(String insertPW, String currentPW) {
        return encoder.matches(insertPW,currentPW);
    }

    public Integer changePw(String changePW, String empId) {

        String enChangePW = encoder.encode(changePW);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("enChangePW",enChangePW);
        updateData.put("empId",empId);

        int result =  userMapper.changePW(updateData);

        return result;
    }

    @Transactional
    public Integer updatePersonalInfo(String email, String phone, String address, String userId,String fileImgName) {

        if (email.equals("null") && phone.equals("null") && address.equals("null") && fileImgName.isEmpty()) {
            System.out.println("업데이트할 데이터가 없습니다.");
            return 0; // 업데이트 실행 안 함
        }

        Map<String, String> updateData = new HashMap<>();

        updateData.put("email",email);
        updateData.put("phone",phone);
        updateData.put("address",address);
        updateData.put("userId",userId);
        updateData.put("fileImgName",fileImgName);

        int result = userMapper.updatePersonalInfo(updateData);

        return result;
    }

    public String getNameById(String empId) {
        return userMapper.getNameById(empId);
    }

    public Map<String, Object> getHiredDateGroupBy() {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> chartData = userMapper.getHiredDateGroupBy();

        for (Map<String, Object> data : chartData){
            String key = String.valueOf(data.get("joined"));
            Integer value = Integer.parseInt(String.valueOf(data.get("num")));

            result.put(key,value);
        }

        return result;
    }

    public int addCheckEvent(LocalDate today, String empId) {
        Map<String,Object> sendData = new HashMap<>();
        sendData.put("today",today);
        sendData.put("empId",empId);

        return userMapper.addCheckEvent(sendData);
    }

    public int getCheckEvent(LocalDate today, String empId) {
        Map<String,Object> sendData = new HashMap<>();
        sendData.put("today",today);
        sendData.put("empId",empId);

        return userMapper.getCheckEvent(sendData);
    }

    public List<String> getAllCheckEvent(LocalDate start, LocalDate end, String empId) {
        Map<String,Object> sendData = new HashMap<>();
        sendData.put("start",start);
        sendData.put("end",end);
        sendData.put("empId",empId);

        return userMapper.getAllCheckEvent(sendData);
    }
}
