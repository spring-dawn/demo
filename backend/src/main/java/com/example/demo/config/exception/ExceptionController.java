package com.example.demo.config.exception;

import com.example.demo.atech.MyUtil;
import com.example.demo.config.exception.shp.ShpInsertChkException;
import com.example.demo.config.exception.shp.ShpInsertDuplException;
import com.example.demo.config.exception.shp.ShpInsertException;
import com.example.demo.dto.ResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.EntityNotFoundException;


@Log4j2
@RestControllerAdvice
public class ExceptionController {
    private final ResponseDto shpInsert;
    private final ResponseDto shpInsertChk;
    private final ResponseDto shpInsertDupl;
    //    private final ObjectMapper objectMapper;
//    private ResponseDto.ResponseRes res500;
    private ResponseDto res500;
//    private ResponseDto.ResponseRes resNotFound;
    private ResponseDto resNotFound;

    public ExceptionController() {
//        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        this.objectMapper = builder.build();

//        this.res500 = new ResponseDto.ResponseRes("now()","INTERNAL_ERROR", "내부 에러 발생");
//        this.resNotFound = new ResponseDto.ResponseRes("now()","NOT_FOUND_ERROR", "정보를 찾을 수 없습니다.");
//        this.res500 = new ResponseDto("500 INTERNAL_SERVER_ERROR", "내부 에러 발생", "");
//        this.resNotFound = new ResponseDto("404 NOT_FOUND","정보를 찾을 수 없습니다.", "");
        this.res500 = new ResponseDto("500", "내부 에러 발생", "");
        this.resNotFound = new ResponseDto("404","정보를 찾을 수 없습니다.", "");
        this.shpInsert = new ResponseDto("SHP_E", "SHP파일을 등록하는 중 문제가 생겼습니다.", "");
        this.shpInsertChk = new ResponseDto("SHP_E_1", "파일의 확장자가 올바르지 않습니다.", "");
        this.shpInsertDupl = new ResponseDto("SHP_E_2", "등록한 성과품의 데이터명이 중복됩니다.", "");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> ServerException(Exception e) {
        log.error("Exception ERROR!" , e);
        res500.setMessage(e.getMessage());
        res500.setTimestamp(MyUtil.timestamp());
        return new ResponseEntity<>(
                this.res500,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> ServerException(EntityNotFoundException e) {
        resNotFound.setTimestamp(MyUtil.timestamp());
        resNotFound.setMessage(e.getMessage());
        return new ResponseEntity<>(
                this.resNotFound,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?>  NotFoundException(Exception e) {
        resNotFound.setTimestamp(MyUtil.timestamp());
        return new ResponseEntity<>(
                this.resNotFound,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ShpInsertException.class)
    public ResponseEntity<?> ShpInsertException(ShpInsertException e) {
        resNotFound.setTimestamp(MyUtil.timestamp());
        return new ResponseEntity<>(
                e.getMessage() == null ? this.shpInsert : new ResponseDto("SHP_E", e.getMessage(), ""),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ShpInsertDuplException.class)
    public ResponseEntity<?> ShpInsertDuplException(ShpInsertDuplException e) {
        resNotFound.setTimestamp(MyUtil.timestamp());
        return new ResponseEntity<>(
                e.getMessage() == null ? this.shpInsertDupl : new ResponseDto("SHP_E_2", e.getMessage(), ""),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ShpInsertChkException.class)
    public ResponseEntity<?> ShpInsertChkException(ShpInsertChkException e) {
        resNotFound.setTimestamp(MyUtil.timestamp());
        String a = e.getMessage();
        return new ResponseEntity<>(
                e.getMessage() == null ? this.shpInsertChk : new ResponseDto("SHP_E_1", e.getMessage(), ""),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}