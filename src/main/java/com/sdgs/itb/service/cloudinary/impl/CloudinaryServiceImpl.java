package com.sdgs.itb.service.cloudinary.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sdgs.itb.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("secure_url").toString(); // Cloudinary URL
    }

    @Override
    public List<String> listAllImages() {
        try {
            Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "resource_type", "image",
                    "max_results", 50
            ));
            List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
            return resources.stream()
                    .map(r -> (String) r.get("secure_url"))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch images from Cloudinary", e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary delete error", e);
        }
    }
}

