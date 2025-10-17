package bitc.full502.lostandfound.util;

import java.io.File;
import java.io.IOException;

public class ImageRenameUtil {

    public static void renameImages(String folderPath, String userId) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("폴더가 존재하지 않거나 디렉토리가 아닙니다: " + folderPath);
        }

        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                    || lower.endsWith(".png") || lower.endsWith(".gif");
        });

        if (files == null || files.length == 0) {
            System.out.println("이미지 파일이 없습니다.");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File oldFile = files[i];
            String extension = oldFile.getName().substring(oldFile.getName().lastIndexOf('.'));
            String newFileName = userId + "_" + (i + 1) + extension;
            File newFile = new File(folder, newFileName);

            if (!oldFile.renameTo(newFile)) {
                System.err.println("파일 이름 변경 실패: " + oldFile.getName());
            } else {
                System.out.println(oldFile.getName() + " -> " + newFileName);
            }
        }
    }
}
