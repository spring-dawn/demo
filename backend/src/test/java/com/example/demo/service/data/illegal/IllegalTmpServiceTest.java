package com.example.demo.service.data.illegal;

import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.domain.data.illegal.file.IllDataRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

import static com.example.demo.atech.ExcelManager.readExcelFile;

@SpringBootTest
//@ActiveProfiles("tmpZ")
class IllegalTmpServiceTest {
    @Autowired
    IllegalTmpService service;

    @Autowired
    IllDataRepository repo;

    @Test
//    @Transactional(readOnly = true)
    void test(){
//        Long id = 9L;
//        IllData origin = repo.findById(id).orElse(null);
//        service.insert(origin);

        System.out.println("test: "+ MyUtil.getIfMsgKey());
    }


}