package com.example.demo.service.data.illegal;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.pk.IllFixedPk;
import com.example.demo.domain.data.illegal.repo.IllFixedRepository;
import com.example.demo.domain.data.monthlyReport.PResi;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllegalDto;
import com.example.demo.dto.data.monthlyReport.PResiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.ExcelManager.readExcelFile;
import static com.example.demo.atech.ExcelManager.writeExcelFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllFixedService {
    /*
    불법주정차 고정형 조회 페이지 서비스
     */

    @Value("${spring.servlet.multipart2.standardExcel.download.ill}")
    private String illPath;

    // TODO: 임시 적발대장 양식?
    private final String EXCEL_NM = "ILLEGAL_MNG.xlsx";

    private final String THIS = "불법주정차단속 고정형";
    private final IllFixedRepository repo;

    // 복합키. insert == update
    public List<IllFixedDto> selectList() {
        return repo.findAll().stream().map(IllFixed::toRes).collect(Collectors.toList());
    }

    public List<IllFixedDto> selectList(String sgg) {
        return repo.findAll().stream().filter(illFixed -> illFixed.getSgg().equals(sgg)).map(IllFixed::toRes).collect(Collectors.toList());
    }


    /*
    proc
     */
    @Transactional
    public IllFixedDto insert(IllFixedDto req) {
        return repo.save(
                IllFixed.builder()
                        .seq(req.getSeq())
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sgg(req.getSgg())
                        .crdnBrnch(req.getCrdnBrnch())
                        .instlYmd(req.getInstlYmd())
                        .crdnPrd(req.getCrdnPrd())
                        .crdnCtrM(req.getCrdnCtrM())
                        .crdnNocs(req.getCrdnNocs())
                        .rmrk(req.getRmrk())
                        .levyAmt(req.getLevyAmt())
                        .clctnNocs(req.getClctnNocs())
                        .clctnAmt(req.getClctnAmt())
                        .build()
        ).toRes();
    }

}
