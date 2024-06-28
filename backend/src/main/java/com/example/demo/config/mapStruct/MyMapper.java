package com.example.demo.config.mapStruct;

import com.example.demo.domain.data.facility.read.PFOpen;
import com.example.demo.domain.data.facility.read.PFPrivate;
import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.IllMobile;
import com.example.demo.domain.data.illegal.read.Illegal;
import com.example.demo.domain.data.monthlyReport.*;
import com.example.demo.domain.survey.data.format.*;
import com.example.demo.domain.survey.data.mngCard.RschSummary;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import com.example.demo.dto.data.illegal.IllegalDto;
import com.example.demo.dto.data.monthlyReport.*;
import com.example.demo.dto.survey.format.*;
import com.example.demo.dto.survey.mngCard.RschSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MyMapper {
    /*
    과하게 많은 컬럼 내용을 엔티티에 자동 매핑(바인딩)하기 위한 라이브러리
     */

    // 실태조사: 관리카드(총괄표)
    RschSummary toRschSummary(RschSummaryDto.Req dto);

    // 실태조사: (조사표) 정리서식
    FormatRd toFormatRd(FormatRdDto dto);
    FormatOut toFormatOut(FormatOutDto dto);
    FormatSub toFormatSub(FormatSubDto dto);
    // 수요조사(노상)
    FormatDmRd toFormatDmRd(FormatDmRdDto dto);
    // 수요조사(노외, 부설, 기타)
    FormatDmEtc toFormatDmEtc(FormatDmEtcDto dto);


    // 데이터관리
    // 주차시설-민영(노외)주차장 현황
    PFPrivate toPFPrivate(PFPrivateDto.Req dto);

    PStatus toPStatus(PStatusDto.Req dto);

    PFOpen toPFOpen(PFOpenDto.Req dto);

    PSubIncrs toPSubIncrs(PSubIncrsDto.Req dto);

    PSubDcrs toPSubDcrs(PSubDcrsDto.Req dto);

    PPublic toPPublic(PPublicDto.Req dto);

    PResi toPResi(PResiDto.Req dto);


    // 불법주정차
    Illegal toIllegal(IllegalDto.Req dto);
    // 임시 불법주정차 단속실적
    IllFixed toIllFixed(IllFixedDto.Req dto);
    IllMobile toIllMobile(IllMobileDto.Req dto);

}
