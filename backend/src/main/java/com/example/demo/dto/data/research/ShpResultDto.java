package com.example.demo.dto.data.research;

import com.example.demo.domain.data.research.shp.ShpResult;
import com.example.demo.domain.system.code.Code;
import com.example.demo.dto.common.FileInfoDto;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShpResultDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShpResultRes {
        private Long resultNo;
        private String name;
        private String year;
        private String regCode;
        private String regName;
        private String type;
        private String subType;
        private String epsg;
        private String color;
        private String icon;

        public void setzIndex(String zIndex) {
            this.zIndex = zIndex;
        }

        private String zIndex;

        public void setColor(String color) {
            this.color = color;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        private String rmrk;
        private String viewYn;
        private String cardYn;
        private String tableName;
        private Integer state;
        private String error;
        private String featureType;
        private List<FileInfoDto.FileInfoRes> files;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShpSearchReq {
        private String name;
        private String year;
        private String reg;
        private String viewYn;
        private String state;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShpResultReq {
        private Long resultNo;
        private String name;
        private String year;
        private String regCode;
        private Code code;
        private String type;
        private String subType;
        private String epsg;
        private String color;
        private String rmrk;
        private String viewYn;
        private String cardYn;
        private String tableName;

        private List<MultipartFile> files;

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public void setCode(Code code) {
            this.code = code;
        }

        public ShpResult toEntity(){
            return ShpResult.builder()
                    .resultNo(this.resultNo)
                    .name(name)
                    .year(year)
                    .type(type)
                    .subType(subType)
                    .epsg(epsg)
                    .color(color)
                    .rmrk(rmrk)
                    .viewYn(viewYn)
                    .cardYn(cardYn)
                    .tableName(tableName)
                    .state(0)
                    .error("")
                    .featureType("")
                    .build();
        }
    }
}
