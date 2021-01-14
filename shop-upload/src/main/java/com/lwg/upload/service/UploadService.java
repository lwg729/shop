package com.lwg.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/14 20:46
 */
public interface UploadService {
    String uploadImage(MultipartFile file) throws IOException;
}
