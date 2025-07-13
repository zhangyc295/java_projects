package com.example.friend.service.impl;

import com.example.common.entity.enums.ResultCode;
import com.example.common.files.OSSResult;
import com.example.common.files.OSSService;
import com.example.common.security.exception.ServiceException;
import com.example.friend.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private OSSService ossService;

    @Override
    public OSSResult upload(MultipartFile file) {
        try {
            long maxSize = 1024 * 1024; // 1MB
            if (file.getSize() > maxSize) {
                throw new ServiceException(ResultCode.FILE_LARGE);
            }
            return ossService.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(ResultCode.FAILED_FILE_UPLOAD);
        }
    }
}
