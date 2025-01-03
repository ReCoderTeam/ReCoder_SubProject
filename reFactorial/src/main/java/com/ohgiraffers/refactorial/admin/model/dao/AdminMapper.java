package com.ohgiraffers.refactorial.admin.model.dao;

import com.ohgiraffers.refactorial.addressBook.model.dto.FactoryDTO;
import com.ohgiraffers.refactorial.admin.model.dto.TktReserveDTO;
import com.ohgiraffers.refactorial.attendance.dto.AttendanceDTO;
import com.ohgiraffers.refactorial.user.model.dto.LoginUserDTO;
import com.ohgiraffers.refactorial.user.model.dto.UserDTO;
import com.ohgiraffers.refactorial.zasaPage.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    // 사원 목록 가져오기
    List<LoginUserDTO> getAllEmployee(Map<String, Object> searchData);

    // 관리자 회원 정보 수정
    Integer modifyEmpInfoUpdate(UserDTO user);

    // 날짜별 근태 전부 가져오기
//    List<AttendanceDTO> getByDateAtt(String selectedDay, int offset, int size);

    // 페이지네이션을 위한 날짜별 데이터 총 개수
    int getTotalCountByDateAtt(Map<String,Object> sendData);

    // 날짜별 근태 전부 가져오기
    List<AttendanceDTO> getByDateAtt(Map<String, Object> sendData);

    // 사원 근태 수정해주기
    Integer modifyEmpAtt(Map<String, Object> sendData);

    List<TktReserveDTO> getTktReserve(String selectedDay);

    TktReserveDTO getReserveById(String reserveId);

    int insertProduct(ProductDTO productDTO);

    String getMaxProductId();

    List<ProductDTO> getAllProducts();

    List<ProductDTO> searchProducts(String keyword);

    ProductDTO getProductById(String id);

    int updateProduct(ProductDTO productDTO);

    int insertFactory(FactoryDTO factoryDTO);

    String getMaxFactoryId();

    int updateFactory(FactoryDTO factoryDTO);

    List<FactoryDTO> getAllFactories();

    List<FactoryDTO> searchFactories(String keyword);

    FactoryDTO findFactoryById(String id);

    Integer getTotalCountTktReserve();
}
