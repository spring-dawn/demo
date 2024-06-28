package com.example.demo.dto.data;

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
public class UploadDataDto {
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
    //
    private String lotType;    // 주차시설 파일관리는 업로드 될 엑셀의 단위가 탭마다 다르므로 구분.
    private String dupType;   // 데이터 중복 수준 알림
    private String dupInfo;
    //
    private String rschType;
//    private List<FileInfoDto.FileInfoRes> attaches;
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
        //
        private String lotType;
        private String dupType;
        private String dupInfo;
        //  실태조사 파일은 1개씩만 올리도록 제한.
        private List<MultipartFile> files;
        // + 실태조사 화면에서 쓸 파일 구분 컬럼
        private String rschType;
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
        private String lotType;
        private String rschType;
    }


}
