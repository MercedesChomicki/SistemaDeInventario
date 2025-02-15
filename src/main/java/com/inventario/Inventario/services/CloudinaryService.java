package com.inventario.Inventario.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.inventario.Inventario.entities.CloudinaryAccount;
import com.inventario.Inventario.repositories.CloudinaryAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final CloudinaryAccountRepository accountRepository;

    @Autowired
    public CloudinaryService(CloudinaryAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Cloudinary getCloudinaryForClient(UUID clientId) {
        CloudinaryAccount account = accountRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", account.getCloudName(),
                "api_key", account.getApiKey(),
                "api_secret", account.getApiSecret()
        ));
    }

    public String uploadImage(UUID clientId, MultipartFile file) throws IOException {
        Cloudinary cloudinary = getCloudinaryForClient(clientId);
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}

