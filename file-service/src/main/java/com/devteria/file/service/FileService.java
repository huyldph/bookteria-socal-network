package com.devteria.file.service;

import com.devteria.file.dto.response.FileData;
import com.devteria.file.dto.response.FileResponse;
import com.devteria.file.exception.AppException;
import com.devteria.file.exception.ErrorCode;
import com.devteria.file.mapper.FileManagementMapper;
import com.devteria.file.repository.FileManagementRepository;
import com.devteria.file.repository.FileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileService {
    FileRepository fileRepository;
    FileManagementRepository fileManagementRepository;
    FileManagementMapper fileManagementMapper;

    public FileResponse uploadFile(MultipartFile file) throws IOException {
        // Store file
        var fileInfo = fileRepository.store(file);

        //Create file management
        var fileManagement = fileManagementMapper.toFileManagement(fileInfo);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        fileManagement.setOwnerId(userId);

        fileManagementRepository.save(fileManagement);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileData downloadFile(String fileName) throws IOException {
        var fileMgmt = fileManagementRepository.findById(fileName).orElseThrow(
                () -> new AppException(ErrorCode.FILE_NOT_FOUND)
        );

        var resource = fileRepository.read(fileMgmt);

        return new FileData(fileMgmt.getContentType(), resource);
    }
}
