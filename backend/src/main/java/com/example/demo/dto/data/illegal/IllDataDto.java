package com.example.demo.dto.data.illegal;

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
public class IllDataDto {
    private Long id;
    private String year;
    private String month;   // 기타 월간보고 등에서 재사용하기 위한 컬럼
    private String sggCd;
    private String dataType;
    private String dataNm;
    private String comment;
    private String collectYn;
    private String createDtm;
    private String createId;
    private List<FileInfoDto.FileInfoRes> files;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        private String year;
        private String month;
        private String sggCd;
        private String dataType;
        private String dataNm;
        private String comment;
        private List<MultipartFile> files;
        //  실태조사 파일은 1개씩만 올리도록 제한.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String year;
        private String sggCd;
        private String month;
        private String dataType;
        private String dataNm;
        private String collectYn;
    }

}
