package com.devteria.file.mapper;

import com.devteria.file.dto.FileInfo;
import com.devteria.file.entity.FileManagement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileManagementMapper {
    @Mapping(target = "id", source = "name")
    FileManagement toFileManagement(FileInfo fileInfo);
}