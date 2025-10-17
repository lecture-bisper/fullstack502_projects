package bitc.full502.lostandfound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String baseUploadPath;

    @Override
    public String uploadFile(MultipartFile file, String subDir, String userId) throws IOException {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getSize() == 0) {
            return null;
        }

        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        String filename = userId + "_" + dateStr + extension;

        // 디렉토리 생성
        File dir = new File(baseUploadPath + subDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("디렉토리 생성 실패: " + dir.getAbsolutePath());
            }
        }

        File dest = new File(dir, filename);
        file.transferTo(dest);

        // URL로 접근할 수 있도록 반환
        return filename;
    }

    @Override
    public void deleteFile(String fileName, String subDir) throws IOException {
        // baseUploadPath는 src 형제 폴더까지의 절대/상대 경로
        // 예: baseUploadPath = "/Users/user/project/upload/"
        String fullPath = baseUploadPath + subDir + "/" + fileName;
        File file = new File(fullPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new IOException("파일 삭제 실패: " + fullPath);
            }
        }
    }
}
