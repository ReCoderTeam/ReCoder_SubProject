package com.ohgiraffers.refactorial.fileUploade.model.service;

import com.ohgiraffers.refactorial.fileUploade.model.dao.UploadFileMapper;
import com.ohgiraffers.refactorial.fileUploade.model.dto.UploadFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UploadFileService {

    private final UploadFileMapper uploadMapper;

    @Autowired
    public UploadFileService(UploadFileMapper uploadMapper) {
        this.uploadMapper = uploadMapper;
    }

    public void upLoadFile(List<MultipartFile> FileList, String mappingId) throws IOException {

        for (MultipartFile file : FileList) {
            String originFileName = file.getOriginalFilename();
            LocalDateTime uploadedAt = LocalDateTime.now();

            long currentTimeMillis = System.currentTimeMillis();

            String storeFileName = currentTimeMillis + "_" + originFileName;

            UploadFileDTO uploadFile = new UploadFileDTO();

            uploadFile.setMappingId(mappingId);
            uploadFile.setOriginFileName(originFileName);
            uploadFile.setStoreFileName(storeFileName);
            uploadFile.setUploadedAt(uploadedAt);
            uploadFile.setFileSize(file.getSize());

            // 확장자 추출
            String fileExtension = "";
            int dotIndex = originFileName.lastIndexOf('.');
            if (dotIndex != -1) {
                fileExtension = originFileName.substring(dotIndex);  // .을 포함한 확장자
            }

            uploadFile.setFileType(fileExtension);

            // 현재 프로젝트 경로 알아오기 reFactorial 까지 출력된다.
            String projectPath = System.getProperty("user.dir");

            String filePath;

            if (fileExtension.equals(".jpg") || fileExtension.equals(".png") || fileExtension.equals(".svg")) {
                filePath = projectPath + "/src/main/resources/static/images/uploadImg/" + storeFileName;
            } else {
                filePath = projectPath + "/src/main/resources/static/files/" + storeFileName;
            }

            // 파일 저장경로에 저장
            file.transferTo(new File(filePath));

            List<UploadFileDTO> existList = uploadMapper.findFileByMappingId(mappingId);

            if (existList != null &&
                    (mappingId.startsWith("C") || mappingId.startsWith("D") ||
                            mappingId.startsWith("M") || mappingId.startsWith("A") || mappingId.startsWith("S") ||
                            (mappingId.length() > 0 && Character.isDigit(mappingId.charAt(0)))))
            {
                System.out.println("mappingId = " + mappingId);
                uploadMapper.deleteByMappingId(mappingId);
                System.out.println("삭제함");
            }

            int uploadResult = uploadMapper.addFile(uploadFile);
        }

    }

    public List<UploadFileDTO> findFileByMappingId(String mappingId) {
        return uploadMapper.findFileByMappingId(mappingId);
    }

    public UploadFileDTO findFileByFileId(String fileId) {
        return uploadMapper.findFileByFileId(fileId);
    }


}