package com.example.demo.dto.data.research;

import com.example.demo.domain.data.research.report.Report;
import com.example.demo.dto.common.FileInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReportDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReportRes {
        private Long reportNo;
        private String name;
        private String year;
        private String regCode;
        private String regName;
        private String rmrk;
        private String createId;
        private String createDtm;
        private FileInfoDto.FileInfoRes file;
        private List<FileInfoDto.FileInfoRes> files;

        public void setFiles(List<FileInfoDto.FileInfoRes> files) {
            this.files = files;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReportSearchReq {
        private String name;
        private String year;
        private String regCode;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReportReq {
        private Long reportNo;
        private String name;
        private String year;
        private String regCode;
        private String rmrk;
        private String pathName;
        private List<MultipartFile> files;

        public void setPathName(String pathName) {
            this.pathName = pathName;
        }

        public Report toEntity(){
            return Report.builder()
                    .reportNo(this.reportNo)
                    .name(name)
                    .rmrk(rmrk)
                    .year(year)
                    .build();
        }
    }
}
