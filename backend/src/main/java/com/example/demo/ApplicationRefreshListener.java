package com.example.demo;

import com.example.demo.atech.MyUtil;
import com.example.demo.config.security.SHA256Util;
import com.example.demo.domain.data.research.shp.ShpResultOption;
import com.example.demo.domain.data.research.shp.ShpResultOptionRepository;
import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.domain.system.menu.Menu;
import com.example.demo.domain.system.menu.MenuRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.domain.system.user.access.*;
import com.example.demo.dto.system.MenuDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    /*
    오버라이드 된 로직을 빌드 시마다 자동 실행합니다. 개발환경에서 샘플 데이터를 넣는 데 사용 중입니다.
     */
    boolean alreadySetup = false;   // flag?

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PrivilegeRepository privilegeRepo;

    private final RolePrivilegeRepository rolePrivilegeRepo;

    private final MenuRepository menuRepo;

    private final CodeRepository codeRepo;

    private final ShpResultOptionRepository optionRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if (userRepo.count() > 0) return;
        /*
       기본값 생성 - MENU, PRIVILEGE / ROLE / USER / CODE

       주차행정 시스템은 별도 권한관리 페이지가 없어 개발자가 직접 권한 정비를 해야 하므로 유지보수가 어렵습니다.
       Role 초기화가 필요한 경우 운영 DB의 계정 정보를 반드시 백업해두고, 수동으로 Role 을 만들고 사용자관리에서 연결해주는 등의 조치가 필요합니다.
         */

//        1. MENU: 상위 페이지 권한이 있는 경우 tab 은 전부 열리게 함. tab 에 대한 권한은 생성x, 검사x.
//        try {
//            MenuDto mMain = createMenuIfNotFound("/main", "메인", null, 0);
//            // 데이터 관리
//            MenuDto mData = createMenuIfNotFound("/data", "데이터관리", null, 1);
//            MenuDto mPf = createMenuIfNotFound(mData.getUrl() + "/pf", "주차시설", mData.getUrl(), 0);
//            MenuDto mPfRead = createMenuIfNotFound(mPf.getUrl() + "/read", "현황 조회", mPf.getUrl(), 0);
//            createTabIfNotFound(mPfRead.getUrl() + "/public", "공영주차장", mPfRead.getUrl(), 0);
//            createTabIfNotFound(mPfRead.getUrl() + "/private", "민영주차장", mPfRead.getUrl(), 1);
////            createTabIfNotFound(mPfRead.getUrl() + "/out", "노외주차장", mPfRead.getUrl(), 2);
//            createTabIfNotFound(mPfRead.getUrl() + "/sub", "부설주차장", mPfRead.getUrl(), 3);
//            createTabIfNotFound(mPfRead.getUrl() + "/pvlOpen", "사유지 개방 주차장", mPfRead.getUrl(), 4);
//            createTabIfNotFound(mPfRead.getUrl() + "/subOpen", "부설 개방 주차장", mPfRead.getUrl(), 5);
//            createTabIfNotFound(mPfRead.getUrl() + "/standardDataset", "표준 데이터셋", mPfRead.getUrl(), 6);
//            MenuDto mPfFile = createMenuIfNotFound(mPf.getUrl() + "/file", "파일 업로드", mPf.getUrl(), 1);
//            //
//            MenuDto mMr = createMenuIfNotFound("/data/monthlyReport", "월간보고", mData.getUrl(), 1);
//            MenuDto mMrRead = createMenuIfNotFound(mMr.getUrl() + "/read", "현황 조회", mMr.getUrl(), 0);
//            // 파일 업로드 화면에는 탭이 없고, 내용 조회 페이지에서는 탭으로 구분.
//            createTabIfNotFound(mMrRead.getUrl() + "/status", "확보 현황", mMrRead.getUrl(), 0);
//            createTabIfNotFound(mMrRead.getUrl() + "/incrs", "증가 현황", mMrRead.getUrl(), 1);
//            createTabIfNotFound(mMrRead.getUrl() + "/dcrs", "감소 현황", mMrRead.getUrl(), 2);
//            createTabIfNotFound(mMrRead.getUrl() + "/public", "공영 현황", mMrRead.getUrl(), 3);
//            createTabIfNotFound(mMrRead.getUrl() + "/resiFirst", "거주자우선 현황", mMrRead.getUrl(), 4);
//            MenuDto mMrFile = createMenuIfNotFound(mMr.getUrl() + "/file", "파일 업로드", mMr.getUrl(), 1);
//            //
//            MenuDto mIll = createMenuIfNotFound("/data/illegal", "불법주정차 단속", mData.getUrl(), 2);
//            MenuDto mIllRead = createMenuIfNotFound(mIll.getUrl() + "/read", "현황 조회", mIll.getUrl(), 0);
//            MenuDto mIllPrfmnc = createMenuIfNotFound(mIll.getUrl() + "/prfmnc", "실적 현황 조회", mIll.getUrl(), 0);
//            MenuDto mIllFile = createMenuIfNotFound(mIll.getUrl() + "/file", "파일 업로드", mIll.getUrl(), 1);
//            //
//            createTabIfNotFound(mIllRead.getUrl() + "/fixed", "고정형", mIllRead.getUrl(), 0);
//            createTabIfNotFound(mIllRead.getUrl() + "/mobile", "이동형", mIllRead.getUrl(), 1);
//            createTabIfNotFound(mIllRead.getUrl() + "/busMounted", "버스탑재형", mIllRead.getUrl(), 2);
//            createTabIfNotFound(mIllRead.getUrl() + "/crackDown", "인력단속", mIllRead.getUrl(), 3);
//            createTabIfNotFound(mIllRead.getUrl() + "/sinmungo", "안전신문고", mIllRead.getUrl(), 4);
//            createTabIfNotFound(mIllRead.getUrl() + "/fireplug", "소화전", mIllRead.getUrl(), 5);
//            createTabIfNotFound(mIllRead.getUrl() + "/protectedArea", "보호구역", mIllRead.getUrl(), 6);
//            //
//            MenuDto mRsch = createMenuIfNotFound("/rsch", "실태조사", null, 2);
//            MenuDto mRschMain = createMenuIfNotFound(mRsch.getUrl() + "/main", "조사결과", mRsch.getUrl(), 0);
//
//            MenuDto mShp = createMenuIfNotFound(mRsch.getUrl() + "/shp", "공간정보(SHP)", mRsch.getUrl(), 1);
//            MenuDto mReport = createMenuIfNotFound(mRsch.getUrl() + "/report", "보고서", mRsch.getUrl(), 2);
//            MenuDto mPlan = createMenuIfNotFound(mRsch.getUrl() + "/floorPlan", "도면", mRsch.getUrl(), 4);
//            MenuDto mMng = createMenuIfNotFound(mRsch.getUrl() + "/mng", "조사자료", mRsch.getUrl(), 3);
//            //
////            MenuDto mMngCard = createMenuIfNotFound(mMng.getUrl() + "/card", "관리카드", mMng.getUrl(), 0);
////            MenuDto mMngFm = createMenuIfNotFound(mMng.getUrl() + "/format", "정리 서식", mMng.getUrl(), 1);
//
//            MenuDto mGis = createMenuIfNotFound("/gis", "GIS 시각화", null, 3);
//            MenuDto mAnaly = createMenuIfNotFound("/analy", "통계/분석", null, 4);
//
//            MenuDto mSys = createMenuIfNotFound("/system", "사용자관리", null, 10);
//            MenuDto mUser = createMenuIfNotFound(mSys.getUrl() + "/user", "사용자관리", mSys.getUrl(), 0);
//            MenuDto myPage = createMenuIfNotFound(mSys.getUrl() + "/myPage", "마이페이지", mSys.getUrl(), 1);
//            MenuDto mFeedback = createMenuIfNotFound(mSys.getUrl() + "/feedback", "시스템 개선 요청", mSys.getUrl(), 2);
//
//            MenuDto myInfo = createMenuIfNotFound(myPage.getUrl() + "/info", "개인정보조회", myPage.getUrl(), 0);
//            MenuDto myPw = createMenuIfNotFound(myPage.getUrl() + "/pw", "비밀번호변경", myPage.getUrl(), 1);
//
////            2. PRIVILEGE(메뉴 단방향 참조). 페이지가 아닌 대, 중분류는 조회권만 만들어도 ok.
//            createPrivilegeIfNotFound("MAIN", "메인", "N", mMain.getId());
//            // *탭에 대한 권한은 따로 생성하지 않음.
//            createPrivilegeIfNotFound("DATA", "데이터관리", "N", mData.getId());
//            createPrivilegeIfNotFound("RSCH", "실태조사", "N", mRsch.getId());
//            createPrivilegeIfNotFound("GIS", "GIS 시각화 조회", "N", mGis.getId());
//            createPrivilegeIfNotFound("GIS", "GIS 시각화 편집", "Y", mGis.getId());
//            createPrivilegeIfNotFound("ANALY", "통계/분석 조회", "N", mAnaly.getId());
//            createPrivilegeIfNotFound("ANALY", "통계/분석 편집", "Y", mAnaly.getId());
//            createPrivilegeIfNotFound("SYSTEM", "사용자관리_1", "N", mSys.getId());
//            // 데이터관리
//            createPrivilegeIfNotFound("PF", "주차시설 현황", "N", mPf.getId());
//            createPrivilegeIfNotFound("PF_FILE", "주차시설 파일관리 조회", "N", mPfFile.getId());
//            createPrivilegeIfNotFound("PF_FILE", "주차시설 파일관리 편집", "Y", mPfFile.getId());
//            createPrivilegeIfNotFound("PF_READ", "주차시설 조회 조회", "N", mPfRead.getId());
//            createPrivilegeIfNotFound("PF_READ", "주차시설 조회 편집", "Y", mPfRead.getId());
//
//            createPrivilegeIfNotFound("MR", "월간보고 현황", "N", mMr.getId());
//            createPrivilegeIfNotFound("MR_FILE", "월간보고 파일관리 조회", "N", mMrFile.getId());
//            createPrivilegeIfNotFound("MR_FILE", "월간보고 파일관리 편집", "Y", mMrFile.getId());
//            createPrivilegeIfNotFound("MR_READ", "월간보고 조회 조회", "N", mMrRead.getId());
//            createPrivilegeIfNotFound("MR_READ", "월간보고 조회 편집", "Y", mMrRead.getId());
//
//            createPrivilegeIfNotFound("ILL", "불법주정차 단속 현황", "N", mIll.getId());
//            createPrivilegeIfNotFound("ILL_FILE", "불법주정차 파일관리 조회", "N", mIllFile.getId());
//            createPrivilegeIfNotFound("ILL_FILE", "불법주정차 파일관리 편집", "Y", mIllFile.getId());
//            createPrivilegeIfNotFound("ILL_READ", "불법주정차 조회 조회", "N", mIllRead.getId());
//            createPrivilegeIfNotFound("ILL_READ", "불법주정차 조회 편집", "Y", mIllRead.getId());
//            createPrivilegeIfNotFound("ILL_PRFMNC", "불법주정차 실적 조회 조회", "N", mIllPrfmnc.getId());
//            createPrivilegeIfNotFound("ILL_PRFMNC", "불법주정차 실적 조회 편집", "Y", mIllPrfmnc.getId());
//
//            // 실태조사
//            createPrivilegeIfNotFound("RSCH_MAIN", "실태조사 메인", "N", mRschMain.getId());
//            createPrivilegeIfNotFound("RSCH_SHP", "공간정보", "N", mShp.getId());
//            createPrivilegeIfNotFound("RSCH_REPORT", "보고서", "N", mReport.getId());
//            createPrivilegeIfNotFound("RSCH_PLAN", "도면", "N", mPlan.getId());
//            createPrivilegeIfNotFound("RSCH_MNG", "조사자료 조회", "N", mMng.getId());
//            createPrivilegeIfNotFound("RSCH_MNG", "조사자료 편집", "Y", mMng.getId());
//            //
////            createPrivilegeIfNotFound("RSCH_MNG_CARD", "관리카드 조회", "N", mMngCard.getId());
////            createPrivilegeIfNotFound("RSCH_MNG_CARD", "관리카드 편집", "Y", mMngCard.getId());
////            createPrivilegeIfNotFound("RSCH_MNG_FM", "정리 서식 조회", "N", mMngFm.getId());
////            createPrivilegeIfNotFound("RSCH_MNG_FM", "정리 서식 편집", "Y", mMngFm.getId());
////
//            createPrivilegeIfNotFound("GIS", "GIS 공간분석 조회", "N", mGis.getId());
//            createPrivilegeIfNotFound("GIS", "GIS 공간분석 편집", "Y", mGis.getId());
////
//            createPrivilegeIfNotFound("SYSTEM", "사용자관리(시스템)", "N", mSys.getId());
//            createPrivilegeIfNotFound("SYSTEM_USER", "사용자관리 조회", "N", mUser.getId());
//            createPrivilegeIfNotFound("SYSTEM_USER", "사용자관리 편집", "Y", mUser.getId());
//            createPrivilegeIfNotFound("SYSTEM_FEEDBACK", "시스템 개선 요청 조회", "N", mFeedback.getId());
//            createPrivilegeIfNotFound("SYSTEM_FEEDBACK", "시스템 개선 요청 편집", "Y", mFeedback.getId());
//            // 보통 마이페이지 편집권 검사는 하지 않습니다.
//            createPrivilegeIfNotFound("SYSTEM_MYPAGE", "마이페이지", "N", myPage.getId());
//            createPrivilegeIfNotFound("SYSTEM_MYPAGE_INFO", "개인정보조회 조회", "N", myInfo.getId());
//            createPrivilegeIfNotFound("SYSTEM_MYPAGE_INFO", "개인정보조회 편집", "Y", myInfo.getId());
//            createPrivilegeIfNotFound("SYSTEM_MYPAGE_PW", "패스워드 변경 조회", "N", myPw.getId());
//            createPrivilegeIfNotFound("SYSTEM_MYPAGE_PW", "패스워드 변경 편집", "Y", myPw.getId());
//
//        } catch (Exception e) {
//           logErr(e);
//        }

//        3. ROLE
        try {
//            1) privileges
            /*
            [24.04.02] 권한 재정비 참고
            멀쩡히 있는 편집권 검사 기능 안 쓰고 프론트에서 편집권 범위 제어하게 뒀다간 권한 검사 요청이 세밀해졌을 때 전역 하드코딩 공사를 해야 할 수도 있음.
            https://docs.google.com/spreadsheets/d/1s4GdzxTkfNQlRltZjzHYJcwzP4CdxQkTXD6Q4VbON-I/edit#gid=0
             */

            List<Privilege> authList = privilegeRepo.findAll();
            List<Privilege> admin = authList.stream()
                    .filter(auth -> !auth.getName().contains("INFO"))
                    .collect(Collectors.toList());

            List<Privilege> sggAdmin = authList.stream()
                    .filter(auth -> !auth.getName().contains("USER"))
                    .collect(Collectors.toList());

            List<Privilege> sggManager = authList.stream()
                    .filter(auth -> !auth.getName().contains("USER") || (auth.getName().contains("RSCH") && auth.getWriteYn().equals("N")))
                    .collect(Collectors.toList());

            List<Privilege> reader = authList.stream()
                    .filter(auth -> auth.getWriteYn().equals("N") && !auth.getName().contains("FEEDBACK") && !auth.getName().contains("USER"))
                    .collect(Collectors.toList());

//            2) role [240411] TODO: 기존 role 을 건드리지 않고 신규 적용할 role 생성해야 함. 이후 기존 롤 삭제 필요. -> swagger 사용 권장
            createRoleIfNotFound("ROLE_ADM2", "(new)시 관리자", "모든 메뉴 조회, 편집 및 사용자관리 가능", admin);
            createRoleIfNotFound("ROLE_SGG_ADM2", "(new)구군 관리자", "데이터관리 업로드 수정/삭제 등 편집 가능.", sggAdmin);
            createRoleIfNotFound("ROLE_SGG_MNGR2", "(new)시/구군 담당자", "데이터관리 업로드 수정/삭제 불가, 등록만 가능", sggManager);
            createRoleIfNotFound("ROLE_READER", "(new)업무 열람자", "시스템 개선 요청 및 편집 불가. 조회, 다운로드만 가능.", reader);

        } catch (Exception e) {
            logErr(e);
        }


//        4. USER
        try {
            Role admin = roleRepo.findByName("ROLE_ADM2").orElseThrow(EntityNotFoundException::new);
            Role sggAdmin = roleRepo.findByName("ROLE_SGG_ADM2").orElseThrow(EntityNotFoundException::new);
            Role sggManager = roleRepo.findByName("ROLE_SGG_MNGR2").orElseThrow(EntityNotFoundException::new);
            Role reader = roleRepo.findByName("ROLE_READER").orElseThrow(EntityNotFoundException::new);

            createUserIfNotFound("admin", "1234", "관리자", "admin@test.com", "31000", "test", "010-1111-1111", admin);
            createUserIfNotFound("test1", "test1", "test1", "test1@test.com", "31100", "test", "010-1111-1111", sggAdmin);
            createUserIfNotFound("test2", "test2", "test2", "test2@test.com", "31400", "test", "010-1111-1111", sggManager);
            createUserIfNotFound("test3", "test3", "test3", "test3@test.com", "31200", "test", "010-1111-1111", reader);
        } catch (Exception e) {
            logErr(e);
        }

//        5. CODE
//        try {
//            // 디폴트: 울산 시군구 법정동 코드
//            Code sgg = createCodeIfNotFound("31000", "울산광역시", "법정동", null);
//
//            // 남구
//            Code namgu = createCodeIfNotFound("31140", "남구", "법정동", sgg.getId());
//            createCodeIfNotFound("3114010100", "무거동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010200", "옥동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010300", "두왕동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010400", "신정동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010500", "달동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010600", "삼산동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010700", "여천동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010800", "야음동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114010900", "선암동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011000", "상개동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011100", "부곡동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011200", "고사동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011300", "성암동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011400", "황성동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011500", "용연동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011600", "남화동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011700", "용잠동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011800", "장생포동", "법정동", namgu.getId());
//            createCodeIfNotFound("3114011900", "매암동", "법정동", namgu.getId());
//
//            // 중구
//            Code junggu = createCodeIfNotFound("31110", "중구", "법정동", sgg.getId());
//            createCodeIfNotFound("3111010100", "학성동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010200", "학산동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010300", "복산동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010400", "북정동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010500", "옥교동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010600", "성남동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010700", "교동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010800", "우정동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111010900", "성안동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011000", "유곡동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011100", "태화동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011200", "다운동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011300", "동동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011400", "서동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011500", "남외동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011600", "장현동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011700", "약사동", "법정동", junggu.getId());
//            createCodeIfNotFound("3111011800", "반구동", "법정동", junggu.getId());
//
//            // 북구
//            Code bukgu = createCodeIfNotFound("31200", "북구", "법정동", sgg.getId());
//            createCodeIfNotFound("312000100", "창평동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000200", "호계동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000300", "매곡동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000400", "가대동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000500", "신천동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000600", "중산동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000700", "상안동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000800", "천곡동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312000900", "달천동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001000", "시례동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001100", "무룡동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001200", "구유동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001300", "정자동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001400", "신명동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001500", "대안동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001600", "당사동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001700", "신현동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001800", "산하동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312001900", "어물동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002000", "명촌동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002100", "진장동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002200", "연암동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002300", "효문동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002400", "양정동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002500", "화봉동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002600", "송정동", "법정동", bukgu.getId());
//            createCodeIfNotFound("312002700", "염포동", "법정동", bukgu.getId());
//
//            // 동구
//            Code donggu = createCodeIfNotFound("31170", "동구", "법정동", sgg.getId());
//            createCodeIfNotFound("3117010100", "방어동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010200", "화정동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010300", "일산동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010400", "전하동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010500", "미포동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010600", "주전동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010700", "동부동", "법정동", donggu.getId());
//            createCodeIfNotFound("3117010800", "서부동", "법정동", donggu.getId());
//
//            // 울주군
//            Code ulju = createCodeIfNotFound("31710", "울주군", "법정동", sgg.getId());
//            createCodeIfNotFound("3171025000", "온산읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171025300", "언양읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171025600", "온양읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171025900", "범서읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171026200", "청량읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171026500", "삼남읍", "법정동", ulju.getId());
//            createCodeIfNotFound("3171031000", "서생면", "법정동", ulju.getId());
//            createCodeIfNotFound("3171034000", "웅촌면", "법정동", ulju.getId());
//            createCodeIfNotFound("3171036000", "두동면", "법정동", ulju.getId());
//            createCodeIfNotFound("3171037000", "두서면", "법정동", ulju.getId());
//            createCodeIfNotFound("3171038000", "상북면", "법정동", ulju.getId());
//            createCodeIfNotFound("3171040000", "삼동면", "법정동", ulju.getId());
//
//            // 성과품 카테고리
//            Code code2 = createCodeIfNotFound("SR", "성과품", "성과품 카테고리", null);
//            Code code2_2 = createCodeIfNotFound("TYPE", "구분", "구분", code2.getId());
//            Code code2_2_1 = createCodeIfNotFound("BASE", "베이스", "베이스", code2_2.getId());
//            createCodeIfNotFound("BASE_1", "구경계", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_2", "법정동", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_3", "행정리", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_4", "지적도", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_5", "블럭경계", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_6", "건물", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_7", "도로", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_8", "도로중심선", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_9", "인구", "베이스", code2_2_1.getId());
//            createCodeIfNotFound("BASE_10", "차량", "베이스", code2_2_1.getId());
//            Code code2_2_2 = createCodeIfNotFound("PARKING", "주차장", "주차장", code2_2.getId());
//            createCodeIfNotFound("PARKING_1", "노상", "주차장", code2_2_2.getId());
//            createCodeIfNotFound("PARKING_2", "노외", "주차장", code2_2_2.getId());
//            createCodeIfNotFound("PARKING_3", "부설", "주차장", code2_2_2.getId());
//            Code code2_2_3 = createCodeIfNotFound("STREET", "수요", "수요", code2_2.getId());
//            createCodeIfNotFound("STREET_1", "노상 수요", "수요", code2_2_3.getId());
//            createCodeIfNotFound("STREET_2", "노외 수요", "수요", code2_2_3.getId());
//            createCodeIfNotFound("STREET_3", "부설 수요", "수요", code2_2_3.getId());
//
//            // 주차장 현황 월간보고
//            Code ps = createCodeIfNotFound("MONTHLY_P", "주차장 월간 보고", "분류 코드", null);
//            Code pPbl = createCodeIfNotFound("PBL", "공영", "대분류", ps.getId());
//            createCodeIfNotFound("PRV", "민영", "대분류", ps.getId());
//            Code pSub = createCodeIfNotFound("SUB", "부설", "대분류", ps.getId());
//            Code pResi = createCodeIfNotFound("RESI", "자가", "대분류", ps.getId());
//            createCodeIfNotFound("ROAD_0", "노상 무료", null, pPbl.getId());
//            createCodeIfNotFound("ROAD_1", "노상 유료", null, pPbl.getId());
//            createCodeIfNotFound("RESI", "거주자", null, pPbl.getId());
//            createCodeIfNotFound("OUT_0", "노외 무료", null, pPbl.getId());
//            createCodeIfNotFound("OUT_1", "노외 유료", null, pPbl.getId());
//            createCodeIfNotFound("SELF_1", "자주식 노면식", null, pSub.getId());
//            createCodeIfNotFound("SELF_2", "자주식 조립식", null, pSub.getId());
//            createCodeIfNotFound("AUTO_1", "기계식 부속", null, pSub.getId());
//            createCodeIfNotFound("AUTO_2", "기계식 전용", null, pSub.getId());
//            createCodeIfNotFound("HOUSE", "단독주택", null, pResi.getId());
//            createCodeIfNotFound("APARTMENT", "공동주택", null, pResi.getId());
//
//            // TODO: ? 주차시설-표준 데이터셋 일련번호 구군
////            Code standardSgg = createCodeIfNotFound("STANDARD_SGG", "표준 데이터셋 일련번호: 구군", "법정동/행정동과 별도 취급", null);
////            createCodeIfNotFound("192", "중구", null, standardSgg.getId());
////            createCodeIfNotFound("193", "남구", null, standardSgg.getId());
////            createCodeIfNotFound("194", "동구", null, standardSgg.getId());
////            createCodeIfNotFound("195", "북구", null, standardSgg.getId());
////            createCodeIfNotFound("196", "울주군", null, standardSgg.getId());
////
////            // 주차시설-표준 데이터셋 일련번호 주차장 종류
////            Code standardSgg = createCodeIfNotFound("STANDARD_LOT_TYPE", "표준 데이터셋 일련번호: 주차장 종류", null, null);
////            createCodeIfNotFound("192", "중구", null, standardSgg.getId());
//
//
//        } catch (Exception e) {
//            logErr(e);
//        }
//
//        //        4. 성과품 구성
//        try {
//            createShpOptionIfNotFound("구경계", "rgba(255, 255, 255, 0.5)", "", "10");
//            createShpOptionIfNotFound("법정동", "rgba(230, 213, 25, 0.7)", "", "12");
//            createShpOptionIfNotFound("행정리", "rgba(90, 25, 230, 0.8)", "", "12");
//            createShpOptionIfNotFound("지적도", "rgba(255,255,255,1)", "", "14");
//            createShpOptionIfNotFound("블럭경계", "rgba(200, 149, 213, 0.54)", "", "13");
//            createShpOptionIfNotFound("건물", "rgba(135, 135, 135, 0.5)", "", "15");
//            createShpOptionIfNotFound("도로", "rgba(58, 233, 172, 0.7)", "", "15");
//            createShpOptionIfNotFound("도로중심선", "rgba(217, 38, 38, 0.8)", "", "16");
//            createShpOptionIfNotFound("인구", "rgba(34, 206, 91, 0.7)", "", "16");
//            createShpOptionIfNotFound("차량", "rgba(255, 0, 0, 0.7)", "", "16");
//
//            createShpOptionIfNotFound("노상", "rgba(195, 239, 113, 0.7)", "", "100");
//            createShpOptionIfNotFound("노외", "rgba(163, 190, 245, 0.7)", "", "100");
//            createShpOptionIfNotFound("부설", "rgba(239, 113, 115, 0.7)", "", "100");
//
//            createShpOptionIfNotFound("노상 수요", "rgba(147, 142, 149, 0.7)", "", "100");
//            createShpOptionIfNotFound("노외 수요", "rgba(51, 73, 153, 0.8)", "", "100");
//            createShpOptionIfNotFound("부설 수요", "rgba(239, 113, 189, 0.7)", "", "100");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("성과품 SHP 옵션 생성에 실패하였습니다.");
//        }

        alreadySetup = true;
    }


    /*
    기본 데이터 생성 메서드. 참조 키가 필요한 경우 return dto.
    pk, uk 등을 비교하여 이미 존재하면 건너뛰며 생성.
     */
    @Transactional
    public MenuDto createMenuIfNotFound(String url, String name, String parentUrl, int seq) throws Exception {
        Optional<Menu> res = menuRepo.findByUrl(url);
        if (res.isPresent()) return res.get().toMenuDto();

        Menu newMenu = Menu.builder()
                .url(url)
                .name(name)
                .parent(parentUrl == null ? null : menuRepo.findByUrl(parentUrl).orElse(null))
                .seq(seq)
//                .tabYn(tabYn) // default: N
//                .useYn(useYn) // default: Y
                .build();
        return menuRepo.save(newMenu).toMenuDto();
    }

    @Transactional
    public MenuDto createMenuIfNotFound(String url, String name, String parentUrl, String useYn, int seq) throws Exception {
        Optional<Menu> res = menuRepo.findByUrl(url);
        if (res.isPresent()) return res.get().toMenuDto();

        Menu newMenu = Menu.builder()
                .url(url)
                .name(name)
                .parent(parentUrl == null ? null : menuRepo.findByUrl(parentUrl).orElse(null))
                .seq(seq)
//                .tabYn(tabYn) // default: N
                .useYn(useYn)
                .build();
        return menuRepo.save(newMenu).toMenuDto();
    }

    @Transactional
    public void createTabIfNotFound(String url, String name, String parentUrl, int seq) {
        if (menuRepo.findByUrl(url).isPresent()) return;
        menuRepo.save(Menu.builder()
                .url(url)
                .name(name)
                .parent(parentUrl == null ? null : menuRepo.findByUrl(parentUrl).orElse(null))
                .tabYn("Y")
                .seq(seq)
                .build()
        );
    }

    @Transactional
    public void createPrivilegeIfNotFound(String name, String encodedNm, String writeYn, Long menuId) {
        Optional<Privilege> optional = privilegeRepo.findByEncodedNm(encodedNm);
        if (optional.isPresent()) return;

        Menu menu = menuRepo.findById(menuId).orElse(null);
        if (menu == null) return;

        Privilege privilege = Privilege.builder()
                .name(name)
                .encodedNm(encodedNm)
                .writeYn(writeYn)
//                .comment(comment)
                .menu(menu)
                .build();
        privilegeRepo.save(privilege);
    }

    @Transactional
    public void createRoleIfNotFound(String name, String encodedNm, String comment, List<Privilege> privileges) {
//        1) 중복 확인
        Optional<Role> optional = roleRepo.findByName(name);
        if (optional.isPresent()) return;

//        2) Role, RolePrivilege 생성
        Role role = Role.builder()
                .name(name)
                .encodedNm(encodedNm)
                .comment(comment)
                .useYn("Y")
                .build();
        roleRepo.save(role);

        for (Privilege p : privileges) {
            RolePrivilege rolePrivilege = RolePrivilege.builder()
                    .role(role)
                    .privilege(p)
                    .build();
            rolePrivilegeRepo.save(rolePrivilege);
        }
    }


    @Transactional
    public void createUserIfNotFound(String userId, String pswd, String userNm, String email, String agency, String dept, String cellNo, Role role) throws NoSuchAlgorithmException {
//        1)
        Optional<User> optional = userRepo.findByUserId(userId);
        if (optional.isPresent()) return;

//        2)
        SHA256Util.PwDto pw = SHA256Util.createPw(pswd);
        User user = User.builder()
                .userId(userId)
                .salt(pw.getSalt())
                .password(pw.getSalted())
                .userNm(userNm)
                .email(email)
                .agency(agency)
                .dept(dept)
                .cellNo(cellNo)
                .pwUpdateDt(LocalDateTime.now())
                .role(role)
                .build();
        userRepo.save(user);
    }

    @Transactional
    public Code createCodeIfNotFound(String name, String value, String comment, Long parentId) {
//        1) 중복 확인
        Optional<Code> optional = codeRepo.findByName(name);
        if (optional.isPresent()) return optional.get();

//        2)
        Code code = Code.builder()
                .name(name)
                .value(value)
                .comment(comment)
                .parent((parentId == null ? null : codeRepo.findById(parentId).orElse(null)))
                .build();
        codeRepo.save(code);

        return code;
    }

    @Transactional
    public ShpResultOption createShpOptionIfNotFound(String subType, String color, String icon, String zi) {
//        1) 중복 확인
        Optional<ShpResultOption> optional = optionRepository.findBySubType(subType);
        if (optional.isPresent()) return optional.get();

//        2)
        ShpResultOption shpResultOption = ShpResultOption.builder()
                .subType(subType)
                .color(color)
                .icon(icon)
                .zIndex(zi)
                .build();

        optionRepository.save(shpResultOption);

        return shpResultOption;
    }

}
