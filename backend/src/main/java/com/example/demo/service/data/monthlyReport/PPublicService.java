package com.example.demo.service.data.monthlyReport;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.monthlyReport.PPublic;
import com.example.demo.domain.data.monthlyReport.repo.PPublicRepository;
import com.example.demo.domain.data.monthlyReport.repoCustom.PPublicRepoCustom;
import com.example.demo.dto.data.monthlyReport.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.ExcelManager.getCellData;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PPublicService {

    @Value("${spring.servlet.multipart2.standardExcel.download.mr}")
    private String mrStdExcelPath;

    public static final String mrStdExcelName = "publicStandard.xlsx";

    private final String EXCEL_NM = "OPEN.xlsx";

    private final String THIS = "공영주차장 현황";
    private final PPublicRepository repo;
    private final PPublicRepoCustom query;

    // search 돌려서 1월/2월/3월치 이렇게 소계를 삼원처럼 보여줄까?
    public List<PPublicDto> selectList() {
        return repo.findAll().stream().map(PPublic::toRes)
                .sorted(Comparator.comparing(PPublicDto::getCreateDtm))
                .collect(Collectors.toList());
    }

    /*
    proc
     */
    @Transactional
    public PPublicDto insert(PPublicDto req) {
        return repo.save(
                PPublic.builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .name(req.getName())
                        .installDt(req.getInstallDt())
                        .location(req.getLocation())
                        .wh(req.getWh())
                        .whSaturday(req.getWhSaturday())
                        .whHoliday(req.getWhHoliday())
                        .dayOff(req.getDayOff())
                        .payYn(req.getPayYn())
                        .pay4Hour(req.getPay4Hour())
                        .pay4Day(req.getPay4Day())
                        .totalSpaces(req.getSpaces() + req.getForDisabled() + req.getForLight() + req.getForPregnant() + req.getForBus() + req.getForElectric())
                        .spaces(req.getSpaces())
                        .forDisabled(req.getForDisabled())
                        .forLight(req.getForLight())
                        .forPregnant(req.getForPregnant())
                        .forBus(req.getForBus())
                        .forElectric(req.getForElectric())
                        .roadYn(req.getRoadYn())
                        .owner(req.getOwner())
                        .agency(req.getAgency())
                        .comment(req.getComment())
                        .build()
        ).toRes();
    }

    @Transactional
    public PPublicDto update(PPublicDto req) {
        PPublic target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.update(req);
        return target.toRes();
    }

    @Transactional
    public PPublicDto delete(Long id) {
        PPublic pbl = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        repo.delete(pbl);
        return pbl.toRes();
    }

    public void excelDownload(HttpServletResponse response, PPublicDto.Keyword req) {
        String excelFileName ="";
        String sggNm = "";
        if (req.getSggCd().equals("31110")) {
            sggNm = "중구";
        }
        else if (req.getSggCd().equals("31140")){
            sggNm ="남구";
        }
        else if (req.getSggCd().equals("31170")){
            sggNm ="동구";
        }
        else if (req.getSggCd().equals("31200")){
            sggNm ="북구";
        }
        else if (req.getSggCd().equals("31710")) {
            sggNm = "울주군";
        }


//        1) 엑셀 생성
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(mrStdExcelPath, mrStdExcelName)) {




            PPublicDto.Keyword pPublicDto =  new PPublicDto.Keyword();
            pPublicDto.setYear(req.getYear());
            pPublicDto.setMonth(req.getMonth());
            pPublicDto.setSggCd(req.getSggCd());

            List<PPublicDto> resPPublicList = query.search(pPublicDto);


            XSSFSheet sheetPPublic = wb.getSheet("공영주차장 현황_표준양식");
            if (sheetPPublic != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPPublic.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 공영주차장 현황");

                row = sheetPPublic.getRow(2);
                row.getCell(3).setCellValue("[울산광역시 " + sggNm + "]");

                for (int i =0; i<resPPublicList.size(); i++){
                    row = sheetPPublic.createRow(i+5);
                    row.createCell(0).setCellValue(i+1);
                    row.createCell(1).setCellValue(resPPublicList.get(i).getMngNo());
                    row.createCell(2).setCellValue(resPPublicList.get(i).getName());
                    row.createCell(3).setCellValue(resPPublicList.get(i).getInstallDt());
                    row.createCell(4).setCellValue(resPPublicList.get(i).getLocation());
                    row.createCell(5).setCellValue(resPPublicList.get(i).getPoint_out());
                    row.createCell(6).setCellValue(resPPublicList.get(i).getArea());
                    row.createCell(7).setCellValue(resPPublicList.get(i).getWh());
                    row.createCell(8).setCellValue(resPPublicList.get(i).getWhSaturday());
                    row.createCell(9).setCellValue(resPPublicList.get(i).getWhHoliday());
                    row.createCell(10).setCellValue(resPPublicList.get(i).getDayOff());
                    row.createCell(11).setCellValue(resPPublicList.get(i).getPayYn().equals("Y") ? "유료" : "무료");
                    row.createCell(12).setCellValue(resPPublicList.get(i).getPay4Hour());
                    row.createCell(13).setCellValue(resPPublicList.get(i).getPay4Day());
                    row.createCell(14).setCellValue(resPPublicList.get(i).getTotalSpaces());
                    row.createCell(15).setCellValue(resPPublicList.get(i).getSpaces());

                    //소계
                    row.createCell(16).setCellValue(resPPublicList.get(i).getForDisabled()
                            +resPPublicList.get(i).getForLight()
                            +resPPublicList.get(i).getForPregnant()
                            +resPPublicList.get(i).getForBus()
                            +resPPublicList.get(i).getForEcho()
                            +resPPublicList.get(i).getForElderly()
                            +resPPublicList.get(i).getForElectric());

                    row.createCell(17).setCellValue(resPPublicList.get(i).getForDisabled());
                    row.createCell(18).setCellValue(resPPublicList.get(i).getForLight());
                    row.createCell(19).setCellValue(resPPublicList.get(i).getForPregnant());
                    row.createCell(20).setCellValue(resPPublicList.get(i).getForBus());
                    row.createCell(21).setCellValue(resPPublicList.get(i).getForEcho());
                    row.createCell(22).setCellValue(resPPublicList.get(i).getForElderly());
                    row.createCell(23).setCellValue(resPPublicList.get(i).getForElectric());
                    row.createCell(24).setCellValue(resPPublicList.get(i).getRoadYn());
                    row.createCell(25).setCellValue(resPPublicList.get(i).getOwner());
                    row.createCell(26).setCellValue(resPPublicList.get(i).getAgency());
                    row.createCell(27).setCellValue(resPPublicList.get(i).getComment());

                }
            }
            else  throw new IllegalArgumentException("'공영주차장 현황_표준양식' 시트가 발견되지 않았습니다.");

            //3) 엑셀 생성, 출력
            //String fileNm = "Swagger 에서는 한글명 제대로 출력중";
            String fileNm = req.getYear()+"년 "+req.getMonth()+"월 "+ "공영주차장 시설 현황";
            ExcelManager.writeExcelFile(response, wb, fileNm);

        } catch (IOException e) {
            log.error("에러");
            e.printStackTrace();
        }
    }

}
