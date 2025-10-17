package bitc.full502.lostandfound.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile file, String subDir, String userId) throws IOException;

    void deleteFile(String fileName, String subDir) throws IOException;
}
