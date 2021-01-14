package com.lwg.upload.controller;

import com.lwg.upload.service.impl.UploadServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 功能描述：品牌图片上传
 *
 * @Author: lwg
 * @Date: 2021/1/14 20:43
 */

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadServiceImpl uploadService;

    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String url = uploadService.uploadImage(file);
        if (StringUtils.isBlank(url)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(url);

    }

}
