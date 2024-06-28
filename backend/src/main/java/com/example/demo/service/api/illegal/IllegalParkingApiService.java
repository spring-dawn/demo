package com.example.demo.service.api.illegal;

import com.example.demo.atech.IllegalDataApiConfig;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.illegal.read.FineLedgerRepository;
import com.example.demo.domain.data.illegal.read.ReceiveDetailRepository;
import com.example.demo.dto.data.illegal.FineLedgerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.demo.atech.MyUtil.timestamp;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllegalParkingApiService {
    /*
    [240322] 차세대 세외수입행정(불법주정차 단속 정보) 데이터 연계. WebClient 사용.
    https://docs.google.com/spreadsheets/d/1UJSzRbpBUj9u3XcszHXmlJnrzAh7HF3HlwDllesGX3Y/edit?gid=0#gid=0

    표준 지침이 제공하는 샘플 코드에서 UrlConnection 을 사용 중. 이 통신 규약에 맞추기 위해 Map 을 중첩 세팅.

    1. 자치단체코드, 단속일자 2가지 파라미터로 '주정차위반 과태료 대장 목록' 요청.
    2. 다건 응답이므로 WebClient 의 flux 로 수신. 스케줄링으로 매일매일 울산시의 주정차위반 과태료 대장을 요청한다면 일별 nn건 정도 예상
    3. 현재 FindLedger 는 '주정차위반 과태료 대장 상세 조회' 기준으로 구성돼있음.
    4. 과태료 내역을 원한다면 대장 목록에서도 충분히 얻을 수 있음.
    5. 주정차위반 과태료 대장 목록 저장, 납부자구분명(코드값으로 변환 필요) + 납부자번호 추출하여 수납상세정보 데이터 요청: flux 수신.
    6. 수납상세 정보 저장
     */

    // 과태료 대장, 수납상세내역 퍼시스턴스
    private final FineLedgerRepository ledgerRepo;
    private final ReceiveDetailRepository receiveRepo;

    // WebClient
    private final IllegalDataApiConfig fetch;

    // 요청 출발지, 요청 도착지(응답)
    @Value("${illegal-data-api.source}")
    private String source;

    @Value("${illegal-data-api.target}")
    private String target;


    // 요청별 인터페이스 ID
    private static final String RECEIVE_DETAIL_IFID = "ERR_Z000012LGS_1741000NIS_0002";
    private static final String FINE_LEDGER_IFID = "ERR_Z000012LGS_1741000NIS_0013";

    // 공통 헤더 상수
    private static final String IF_TYPE = "S";
    private static final String IF_FORMAT = "J";


    // TODO:

    //    @Transactional
//    @Scheduled(cron = "오늘자를 오후에or어제자를 새벽에", zone = "Asia/Seoul")
    public void callFineTest() {
        log.info("1. '주정차위반 과태료 대장 목록' 호출합니다. 현재시각={}", timestamp());

        // 과태료 대장 목록 조회 파라미터 세팅
        Map<String, String> header4Fine = setHeader(FINE_LEDGER_IFID);
        Map<String, Object> body = setBody(setRequest4Fine(null));

        // exec
        List<FineLedgerDto> fineList = new ArrayList<>();
        fetch.callApi2FineLedger(fetch.template(), header4Fine, body).subscribe(
                /*
//                인자의 이름과 상관없이 들어온 순서대로 역할 부여, 콜백 처리. JS 의 비동기함수 처리 방식과 동일합니다.
//                1: 각 응답의 개별 처리
//                2: 에러 핸들링(요청 자체가 아닌 1을 대상으로 함)
//                3: 요청이 완료된 시점의 작업
//                 */
                fineList::add,  // list 에 응답 데이터 add
                err -> log.error("요청 후 결과 데이터를 처리하던 중 예외 발생했습니다.", err),    // 응답 데이터 add 중 에러 발생하면 처리
                () -> {
                    // 성공 시
                    log.info("asdf fineList={}", fineList);
                    log.info("2. '수납상세정보' 호출합니다. 현재시각={}", timestamp());

                    Map<String, String> receiveReq = setRequest4Receive("", "", "");
                    fetch.callApi2ReceiveDetail(fetch.template(), setHeader(RECEIVE_DETAIL_IFID), setBody(receiveReq)).subscribe(
                            // TODO: 수납 내역 데이터 처리

                    );
                }
        );

        log.info("데이터 적재 완료");
    }


    //    ---------------------------------------------------------------------------()
    private Map<String, String> setHeader(String ifId) {
        HashMap<String, String> header = new LinkedHashMap<>();

        header.put("ifDate", MyUtil.getIfDate());
        header.put("ifMsgKey", MyUtil.getIfMsgKey());
        header.put("ifId", ifId);
        header.put("source", source);
        header.put("target", target);
        header.put("ifType", IF_TYPE);
        header.put("ifFormat", IF_FORMAT);

        return header;
    }

    private Map<String, Object> setBody(Map<String, String> req) {
        HashMap<String, Object> body = new LinkedHashMap<>();
        body.put("reqVo", req);

        return body;
    }

    private Map<String, String> setRequest4Fine(String pagingKey) {
        HashMap<String, String> req = new LinkedHashMap<>();
        //
        req.put("sgbCd", "6310000");
        req.put("rgtnYmd", MyUtil.getRgtnYmd());
        req.put("prkgVltAcbKey", hasText(pagingKey) ? pagingKey : "");

        return req;
    }

    // TODO: 수납상세내역 조회용 파라미터 세팅
    private Map<String, String> setRequest4Receive(String dmndClCd, String pyrSeCd, String pyrNo) {
        HashMap<String, String> req = new LinkedHashMap<>();
        //
        req.put("sgbCd", "6310000");
        req.put("dmndClCd", dmndClCd);
        req.put("pyrSeCd", pyrSeCd);
        req.put("pyrNo", pyrNo);

        return req;
    }


}
