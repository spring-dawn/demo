package com.example.demo.dto.data.research;

import com.example.demo.domain.data.research.floorPlan.FloorPlan;
import com.example.demo.dto.common.FileInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FloorPlanDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FloorPlanRes {
        private Long fpNo;
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
    public static class FloorPlanSearchReq {
        private String name;
        private String year;
        private String regCode;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FloorPlanReq {
        private Long fpNo;
        private String name;
        private String year;
        private String regCode;
        private String rmrk;
        private List<MultipartFile> files;

        public FloorPlan toEntity(){
            return FloorPlan.builder()
                    .fpNo(fpNo)
                    .name(name)
                    .rmrk(rmrk)
                    .year(year)
                    .build();
        }
    }
}
