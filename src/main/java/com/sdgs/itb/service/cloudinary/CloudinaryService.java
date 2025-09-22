package com.sdgs.itb.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
    List<String> listAllImages();
    void deleteFile(String publicId);
}

