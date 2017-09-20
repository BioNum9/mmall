package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by lovea on 2017/7/17.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
