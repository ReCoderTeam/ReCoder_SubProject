package com.ohgiraffers.refactorial.admin.model.service;

import com.ohgiraffers.refactorial.addressBook.model.dto.FactoryDTO;
import com.ohgiraffers.refactorial.admin.model.dao.AdminMapper;
import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.zasaPage.model.dto.ProductDTO;
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
    @Autowired
    private AdminMapper adminMapper;

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

    public Integer getTotalCountTktReserve(){
        return am.getTotalCountTktReserve();
    }

    public TktReserveDTO getReserveById(String reserveId) {
        return am.getReserveById(reserveId);
    }

    public int addProduct(ProductDTO productDTO) {
        return adminMapper.insertProduct(productDTO);
    }

    public String generateProductId() {
        // 데이터베이스에서 가장 큰 제품 ID 가져오기
        String maxId = adminMapper.getMaxProductId();

        // 데이터베이스에 아무 데이터가 없을 경우 기본값 "PD001" 반환
        if (maxId == null || maxId.isEmpty()) {
            return "PD001"; // 데이터베이스에 아무 것도 없을 때 첫 ID
        }

        // maxId 에서 숫자 부분만 추출하고 정수로 변환한 후 1 증가
        int newIdNum = Integer.parseInt(maxId.substring(2)) + 1;

        // 새로운 ID를 "PD" 접두사와 3자리 숫자로 조합
        return String.format("PD%03d", newIdNum);}



    public List<ProductDTO> getAllProducts() {
        return adminMapper.getAllProducts();
    }

    public List<ProductDTO> searchProducts(String keyword) {
        return adminMapper.searchProducts(keyword);
    }

    public ProductDTO getProductById(String id) {
        return adminMapper.getProductById(id);
    }

    public int updateProduct(ProductDTO productDTO) {
        return adminMapper.updateProduct(productDTO);
    }

    public int addFactory(FactoryDTO factoryDTO) {
        return adminMapper.insertFactory(factoryDTO);
    }

    public String generateFactoryId() {
        String maxId = adminMapper.getMaxFactoryId();
        if (maxId == null || maxId.isEmpty()) {
            return "FAB001";
        }

        int newIdNum = Integer.parseInt(maxId.substring(3)) + 1;

        System.out.println("String.format(\"FAB%03d\", newIdNum); = " + String.format("FAB%03d", newIdNum));

        return String.format("FAB%03d", newIdNum);
    }


    public int updateFactory(FactoryDTO factoryDTO) {
        return adminMapper.updateFactory(factoryDTO);
    }


    public List<FactoryDTO> getAllFactories() {
        return adminMapper.getAllFactories();
    }

    public List<FactoryDTO> searchFactories(String keyword) {
        return adminMapper.searchFactories(keyword);
    }


    public FactoryDTO getFactoryById(String id) {
        return adminMapper.findFactoryById(id);
    }
}
