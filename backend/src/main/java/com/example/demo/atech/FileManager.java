package com.example.demo.atech;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileManager {
    /*
    FileInfo 와 별개로 실제 파일 처리에 대한 유틸을 분리합니다.
    실제 저장소의 파일 저장/삭제/이동 등을 처리합니다.
     */
    @Value("${spring.servlet.multipart.location}")
    private String FILE_DIR;

    @Value("${spring.servlet.multipart2.shp}")
    private String SHP_DIR;

    private static String[] SHP = {".shp", ".shx", ".dbf", ".prj", ".sbx", ".sbn", ".idx", ".xml", ".qmd", ".cpg", ".bak"};

    public Res saveFile(MultipartFile file) {
        Res result = null;

        try {
            // 원본명, uuid, 확장자 등 정보 분리.
            String originNm = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String ext = MyUtil.getFileNmOrExt(originNm, false);
            String savedNm = uuid + ext;

            file.transferTo(new File(FILE_DIR + savedNm));
            // 리턴 정보
            result = Res.builder()
                    .originNm(originNm)
                    .savedNm(savedNm)
                    .ext(ext)
                    .path(FILE_DIR)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Res> saveFiles(List<MultipartFile> files) {
        List<Res> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(saveFile(file));
        }
        return result;
    }

    public Res saveShpFile(MultipartFile file, String path) {
        Res result = null;

        try {
            // 원본명, uuid, 확장자 등 정보 분리.
            String originNm = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String ext = MyUtil.getFileNmOrExt(originNm, false);
            String savedNm = uuid + ext;

            // shp 인 경우 원본명 유지, 별도 폴더에 저장.
            String dir = SHP_DIR + path + "/";
            MyUtil.mkDirAuto(dir);
            file.transferTo(new File(dir + originNm));

            // 리턴 정보
            result = Res.builder()
                    .originNm(originNm)
                    .savedNm(savedNm)
                    .ext(ext)
                    .path(dir)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Res> saveShpFiles(List<MultipartFile> files, String path) {
        List<Res> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(saveShpFile(file, path));
        }
        return result;
    }

    /**
     * 파일 삭제. FileInfo 의 path + 실제 저장된 이름으로 타겟팅.
     *
     * @param path   대상 경로 ex) /test/TMP/
     * @param fileNm 확장자 포함한 파일명
     */
    public boolean rmFile(String path, String fileNm) {
        File target = new File(path + fileNm);
        try {
            if (!target.exists()) throw new FileNotFoundException(Msg.NO_FILES.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target.delete();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Res {
        // 본래 파일명, 실제 저장명, 확장자, 저장 경로
        private String originNm;
        private String savedNm;
        private String ext;
        private String path;
    }

}
