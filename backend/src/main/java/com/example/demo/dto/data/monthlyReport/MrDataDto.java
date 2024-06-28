package com.example.demo.dto.data.monthlyReport;

import com.example.demo.dto.common.FileInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MrDataDto {
    /*
    사실상 파일관리 공통 dto 로 사용중...
     */
    private Long id;
    private String year;
    private String month;   // 기타 월간보고 등에서 재사용하기 위한 컬럼
    private String sggCd;
    private String dataNm;
    private String comment;
    private String collectYn;


    private String dupType;
    private String dupInfo;



    //
    //private List<FileInfoDto.FileInfoRes> attaches;
    private List<FileInfoDto.FileInfoRes> files;
    private String createDtm;
    private String createId;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        private String year;
        private String month;
        private String sggCd;
        private String dataNm;
        private String comment;

        private String collectYn;
        private String dupType;
        private String dupInfo;


        private List<MultipartFile> files;
        private String createDtm;
        private String createId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String year;
        private String sggCd;
        private String month;
        private String dataNm;
        private String collectYn;
    }


}
