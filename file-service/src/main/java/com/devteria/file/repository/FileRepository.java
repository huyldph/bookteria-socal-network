package com.devteria.file.repository;

import com.devteria.file.dto.FileInfo;
import com.devteria.file.entity.FileManagement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Repository
public class FileRepository {
    @Value("${app.file.storage-dir}")
    String storageDir;

    @Value("${app.file.download-prefix}")
    String urlPrefix;

    public FileInfo store(MultipartFile file) throws IOException {
        // Implement the logic to upload the file
        Path folder = Paths.get(storageDir);

        String fieExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fieExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fieExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return FileInfo.builder()
                .name(fileName)
                .size(file.getSize())
                .contentType(file.getContentType())
                .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .path(filePath.toString())
                .url(urlPrefix + fileName)
                .build();
    }

    public Resource read(FileManagement fileManagement) throws IOException {
        var data = Files.readAllBytes(Path.of(fileManagement.getPath()));

        return new ByteArrayResource(data);
    }
}
