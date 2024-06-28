package com.example.demo.atech;

import jj2000.j2k.util.StringFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
public class MyUtil {
    /*
    잡다한 유틸리티
     */
    private MyUtil(){
        // 인스턴스화를 방지하기 위한 private 생성자
    }

    // 날짜 포매터
    private static final DateTimeFormatter ifDateFm = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter ifMsgKeyFm = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
    private static final DateTimeFormatter rgtnYmdFm = DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     * 현재 시각 출력
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * createDtm 을 yyyy-MM-dd HH:mm:ss 패턴의 문자열로 변환
     * @param dtm LocalDateTime createDtm(CommonEntity)
     * @return null or Str
     */
    public static String createDtm2Str(LocalDateTime dtm) {
        String result = null;
        if (dtm != null) result = dtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return result;
    }

    /**
     * 문자열로 받은 날짜 정보를 LocalDate 로 변환
     * @param dt "yyyy-MM-dd"
     * @return LocalDate or null
     */
    public static LocalDate parse2Dt(String dt) {
        return hasText(dt) ? LocalDate.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    public static LocalDateTime parse2Dtm(String dtm){
        return hasText(dtm) ? LocalDateTime.parse(dtm, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }

    /**
     * enum 동적 재사용 메시지 불러오기
     * @param type enum Msg 클래스에 선언된 것중 사용할 양식
     * @param str %s 자리에 들어갈 특정 문자열 혹은 엔티티명 ex) 사용자, 권한, 코드...
     * @return 조합된 메시지 문자열
     */
    public static String getEnum(Msg type, String str) {
        return String.format(type.getMsg(), str);
    }


    /**
     * 확장자 없는 순수 파일명 or 확장자
     * @param fileNm 파일명 전체
     * @param nameOrExt boolean
     * @return T: 파일명, F: 확장자
     */
    public static String getFileNmOrExt(String fileNm, boolean nameOrExt) {
        if (!StringUtils.hasText(fileNm)) throw new StringFormatException("올바른 파일명이 아닙니다. 다시 확인해주세요.");

        if (nameOrExt) {
            try {
                int lastDotIndex = fileNm.lastIndexOf('.');

                if (lastDotIndex != -1) {
                    String nameWithoutExtensions = fileNm.substring(0, lastDotIndex);

                    // 다시 추출한 값에 확장자가 더 있을 경우 반복해서 제거
                    while (nameWithoutExtensions.lastIndexOf('.') != -1) {
                        int dotIndex = nameWithoutExtensions.lastIndexOf('.');
                        nameWithoutExtensions = nameWithoutExtensions.substring(0, dotIndex);
                    }

                    return nameWithoutExtensions;
                } else {
                    // 확장자가 없는 경우, 파일 이름 자체를 반환
                    return fileNm;
                }

            } catch (StringIndexOutOfBoundsException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            return fileNm.substring(fileNm.lastIndexOf("."));
        }
    }

    /**
     * 필요한 경로가 존재하지 않는 경우 새로 만들기
     * @param path 새로 만들 폴더 경로
     */
    public static boolean mkDirAuto(String path) {
        boolean isSuccess = true;

        File dir = new File(path);
        if (!dir.exists()) {
            isSuccess = dir.mkdirs();
        }

        return isSuccess;
    }


    /**
     * 구군코드(311~ 5자리) -> 구군명으로 변환. 유효값이 아니면 IllegalArgumentException 발생.
     * @param sggCd 5자리 구군코드. 코드 테이블 참고.
     * @return 중/남/북/동구, 울주군 등의 구군명
     */
    public static String getSggCd2Nm(String sggCd) {
        String sggNm;
        switch (sggCd) {
            case "31110":
                sggNm = "중구";
                break;
            case "31140":
                sggNm = "남구";
                break;
            case "31200":
                sggNm = "북구";
                break;
            case "31170":
                sggNm = "동구";
                break;
            case "31710":
                sggNm = "울주군";
                break;
            case "31000":
                sggNm = "본청";
                break;
            default:
                // 입력 유효값이 아님
                throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
        }
        return sggNm;
    }

    /**
     * 주차시설 주차장 유형을 세부 분류 ex) "1" -> "공영", "노상"
     * @param lotType 주차시설 주차유형 ex) "1", "2"...
     * @return map. ex) "section" : 공영, "type" : 노상
     */
    public static HashMap<String, String> getLotSectionAndType(String lotType) {
        HashMap<String, String> map = new HashMap<>();

        String section = null;
        String type;
        switch (lotType) {
            case "1":
                section = "공영";
                type = "노상";
                break;
            case "2":
                section = "공영";
                type = "노외";
                break;
            case "3":
                section = "공영";
                type = "부설";
                break;
            case "4":
                section = "민영";
                type = "노상";
                break;
            case "5":
                section = "민영";
                type = "노외";
                break;
            case "6":
                section = "민영";
                type = "부설";
                break;
            case "7":
                type = "부설";
                break;
            case "8":
//                    section = "공영";
                type = "부설개방";
                break;
            case "9":
                type = "사유지개방";
                break;
            default:
                throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
        }
        map.put("section", section);
        map.put("type", type);
        return map;
    }

    /**
     * 표준 주차시설 엑셀 표준 파일명 짓기
     * @param year
     * @param month
     * @param sggCd
     * @param lotType 주차유형 코드값
     * @return 보고서 업로드용 표준 파일명
     */
    public static String makeStandardFileNm4Pf(String year, String month, String sggCd, String lotType) {
        // [구/군]_[주차유형]_주차시설 표준_[yyyyMM].xlsx
        String sggNm = getSggCd2Nm(sggCd) + "_";
        String standard = "_주차시설 표준_";

        HashMap<String, String> map = getLotSectionAndType(lotType);
        String section = map.get("section") == null ? "" : map.get("section");
        String type = map.get("type");

        return sggNm + section + type + standard + year + month;
    }

    /**
     * 표준 월간보고 엑셀 표준 파일명 짓기
     * @param year
     * @param month
     * @param sggCd
     * @return
     */
    public static String makeStandardFileNm4Mr(String year, String month, String sggCd) {
        // [구/군]_월간보고 표준_[yyyyMM].xlsx
        String sggNm = getSggCd2Nm(sggCd);
        return sggNm + "_월간보고 표준_" + year + month;
    }

    public static String makeStandardFileNm4TmpIllegal(String year, String month, String sggCd) {
        // [구/군]_불법주정차 단속실적_[yyyyMM].xlsx
        String sggNm = getSggCd2Nm(sggCd);
        return sggNm + "_불법주정차 단속실적_" + year + month;
    }


    /**
     * String to Long
     * @param param String
     * @return null, "" 공백 검사 후 변환
     */
    public static Long hasLong(String param) {
        return !hasText(param) ? 0 : Long.parseLong(param);
    }

    /**
     * String to Double
     * @param param String
     * @return null, "" 공백 검사 후 변환
     */
    public static Double hasDouble(String param) {
        return !hasText(param) ? 0.0 : Double.parseDouble(param);
    }

    /**
     * String to Integer
     * @param param String
     * @return null, "" 공백 검사 후 변환
     */
    public static Integer hasInteger(String param) {
        return !hasText(param) ? 0 : Integer.parseInt(param);
    }


    /**
     * 예외 로깅
     * @param e 예외 객체
     */
    public static void logErr(Exception e){
        log.error("Error!", e);
    }


    /**
     * 불법주정차 데이터 요청 시 헤더 ifDate 값 생성
     * @return ifDate
     */
    public static String getIfDate(){
        return LocalDateTime.now().format(ifDateFm);
    }

    /**
     * 불법주정차 데이터 요청 시 헤더 ifMsgKey 값 생성
     * @return ifMsgKey
     */
    public static String getIfMsgKey() {
        /*
            연계 메시지 키
            각 연계 메시지의 식별을 위한 고유 식별키. 이용기관과 제공기관 간 메시지키를 통한 정보 조회 시 필요.
            [형식 : 시스템코드 + 년월일시분초 (yyMMddHHmmssSSS )+“-“+자바 UUID (＇-＇제외 )]
            [시스템코드 : 세무행정 (T), 대국민 (W), 세외수입 (N), 행정지원 (A), 연계 (L), 유관기관 (E), 개별/특화시스템 (Z)
            [예시 : Z211103155123435-e7def4c1652a478e9c77525ab5b3ebda ]
         */
        String formatted = LocalDateTime.now().format(ifMsgKeyFm);
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return "Z" + formatted + "-" + uuid;
    }

    public static String getRgtnYmd(){
        return LocalDateTime.now().format(rgtnYmdFm);
    }



}
