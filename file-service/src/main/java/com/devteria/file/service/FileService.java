package com.devteria.file.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {
    public Object uploadFile(MultipartFile file) throws IOException {
        // Implement the logic to upload the file
        Path folder = Paths.get("D:/uploads");
        String fieExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fieExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fieExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return null;
    }
}
