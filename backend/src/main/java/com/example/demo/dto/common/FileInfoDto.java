package com.example.demo.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class FileInfoDto {
    private Long id;
    private String fileNm;
    private String fileNmStored;
    private String filePath;


    @Getter
    @Builder
    @AllArgsConstructor
    public static class FileInfoRes {
        private Long id;
        private String fileNm;
        private String fileNmStored;
        private String filePath;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShpFileRes {
        private Long id;
        private String fileNm;
        private String tablePath;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShpReq {
        private MultipartFile file;
        private String initName;
    }
}
