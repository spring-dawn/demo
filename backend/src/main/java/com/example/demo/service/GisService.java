package com.example.demo.service;

import com.example.demo.atech.FileManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.config.exception.shp.ShpInsertChkException;
import com.example.demo.config.exception.shp.ShpInsertException;
import com.example.demo.domain.api.building_management.*;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.gis.GisCustomQuery;
import com.example.demo.domain.data.monthlyReport.repoCustom.PPublicRepoCustom;
import com.example.demo.domain.data.research.report.ReportRepository;
import com.example.demo.domain.data.research.shp.ShpResult;
import com.example.demo.domain.data.research.shp.ShpResultOption;
import com.example.demo.domain.data.research.shp.ShpResultOptionRepository;
import com.example.demo.domain.data.research.shp.ShpResultRepository;
import com.example.demo.domain.data.standardSet.StandardMngRepoCustom;
import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.dto.GeoJSONResponse;
import com.example.demo.dto.GisDto;
import com.example.demo.dto.common.FileInfoDto;
import com.example.demo.dto.data.monthlyReport.PPublicDto;
import com.example.demo.dto.data.research.ShpResultDto;
import com.example.demo.dto.data.standard.StandardSetDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.api.fh.FireHydrantApiService;
import com.example.demo.service.api.pa.ProtectedAreaApiService;
import com.example.demo.service.api.pubPk.PubPkApiService;
import com.example.demo.service.common.FileService;
import com.example.demo.service.data.illegal.IllFixedService;
import com.example.demo.service.data.research.ShpResultService;
import com.example.demo.service.data.residentXy.ResidentXyService;
import com.example.demo.service.data.standard.StandardMngService;
import com.example.demo.service.system.CodeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.StandardService;
import org.geotools.data.*;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.json.JSONObject;
import org.json.XML;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GisService{
    private final ReportRepository reportRepository;
    private final BuildingManagementRepository buildingManagementRepository;
    private final CodeRepository codeRepository;
    private final CodeService codeService;
    private final DataSource dataSource;
    private final ResidentXyService residentXyService;
    private final PubPkApiService pubPkApiService;
    private final FireHydrantApiService fireHydrantApiService;
    private final ProtectedAreaApiService protectedAreaApiService;
    private final IllFixedService illFixedService;

    private final PPublicRepoCustom pPublicRepoCustom;

    private final StandardMngRepoCustom standardMngRepoCustom;
    private final StandardMngService stdMngService;

    /*
    GIS 데이터 서비스. 너무 길어서 FileService 에서 분리합니다.
     */

    /* shapefile 구분 */
    public static String[] SHP_FILE = {".shp", ".shx", ".dbf", ".prj", ".sbx",".sbn",".idx",".xml",".qmd",".cpg",".bak",".qpj"};

    /* geoserver 정보 */
    @Value("${spring.geoserver.domain}")
    public String GEOSERVER_DOMAIN;
    @Value("${spring.geoserver.workspace}")
    public String GEOSERVER_WORKSPACE;
    @Value("${spring.geoserver.user}")
    public String GEOSERVER_USER;
    @Value("${spring.geoserver.pass}")
    public String GEOSERVER_PASS;

    /* 직접 연결 postgres 정보 */
    @Value("${spring.connect-datasource.host}")
    public String DATASOURCE_HOST;
    @Value("${spring.connect-datasource.port}")
    public String DATASOURCE_PORT;
    @Value("${spring.connect-datasource.user}")
    public String DATASOURCE_USER;
    @Value("${spring.connect-datasource.pass}")
    public String DATASOURCE_PASS;
    @Value("${spring.servlet.multipart2.tmp}")
    private String tmpDir;
    @Value("${spring.servlet.multipart2.shp}")
    private String fileDirSHP;
    /* API 파일 저장소 */
    @Value("${spring.servlet.multipart2.json}")
    private String jsonDir;

    private final FileInfoRepository fileInfoRepo;
    private final FileService fileService;
    private final FileManager fileManager;
    private final ShpResultRepository shpResultRepository;
    private final ShpResultOptionRepository shpResultOptionRepository;
    private final ShpResultService shpResultService;

    private final GisCustomQuery gisCustomQuery;

    private Map<String, Object> connectPostgreDb() {
        Map<String, Object> params = new HashMap<>();
        params.put(PostgisNGDataStoreFactory.DBTYPE.key, "postgis");
        params.put(PostgisNGDataStoreFactory.HOST.key, DATASOURCE_HOST);
        params.put(PostgisNGDataStoreFactory.PORT.key, DATASOURCE_PORT);
        params.put(PostgisNGDataStoreFactory.SCHEMA.key, "public");
        params.put(PostgisNGDataStoreFactory.DATABASE.key, GEOSERVER_WORKSPACE);
        params.put(PostgisNGDataStoreFactory.USER.key, DATASOURCE_USER);
        params.put(PostgisNGDataStoreFactory.PASSWD.key, DATASOURCE_PASS);
        return params;
    }

    ExecutorService executor = Executors.newFixedThreadPool(6);
    public String[] gugunList = new String[]{
            "중구",
            "동구",
            "남구",
            "북구",
            "울주군",
    };

    public String[] pngLayerList = new String[]{
            "법정동",
            "행정리",
            "건물",
            "지적도",
            "도로",
            "도로중심선",
            "구경계",
    };

    // GIS 검색 (요소)
    @Transactional
    public ArrayList<HashMap<String, Object>> searchObject(List<Map<String, String>> searchTree, String dong) throws JsonProcessingException {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();

        for (Map<String, String> reqMap : searchTree) {
            HashMap<String, Object> resultMap = new HashMap<>();

            String shpTable = reqMap.get("tree0");

            GeoJSONResponse geoJSONResponse = null;
            ShpResultDto.ShpResultRes toShpResultRes = shpResultRepository.findByTableName(shpTable).toShpResultRes();

            // 주차시설이나 주차수요일 경우 커스텀 쿼리
            if (toShpResultRes.getType().equals("주차장") || toShpResultRes.getType().equals("수요")) {
                CodeDto dongCode = null;

                if (!dong.isEmpty()) {
                    dongCode = codeService.selectCodeByName(dong);
                }

                GeoJSONResponse customGeoJSONResponse = gisCustomQuery.customGeoJsonQuery(
                        shpTable,
                        toShpResultRes.getType(),
                        toShpResultRes.getSubType(),
                        toShpResultRes.getYear(),
                        toShpResultRes.getRegCode(),
                        toShpResultRes.getEpsg(),
                        dongCode
                );

                geoJSONResponse = customGeoJSONResponse;
            }

            // 이미지 레이어가 아니면 가져오기
            else if (!Arrays.asList(pngLayerList).contains(toShpResultRes.getSubType())) {
                geoJSONResponse = searchFn(
                        shpTable,
                        null,
                        null
                );
            }

            // 색상, 아이콘 설정
            Optional<ShpResultOption> optional = shpResultOptionRepository.findBySubType(toShpResultRes.getSubType());
            if (optional.isPresent()) {
                ShpResultOption shpResultOption = optional.get();
                toShpResultRes.setColor(shpResultOption.getColor());
                toShpResultRes.setIcon(shpResultOption.getIcon());
                toShpResultRes.setzIndex(shpResultOption.getZIndex());

            } else {
                toShpResultRes.setColor("rgba(255, 255, 255, 1)");
                toShpResultRes.setIcon("");
                toShpResultRes.setIcon("20");
            }

            resultMap.put("shpInfo", toShpResultRes);
            resultMap.put("layer", geoJSONResponse);

            resultList.add(resultMap);
        }

        return resultList;
    }

    // GIS 검색 (시도) 사용X
    public ArrayList<GeoJSONResponse> searchSido() throws JsonProcessingException {
        ArrayList<GeoJSONResponse> resultList = new ArrayList<>();

        GeoJSONResponse geoJSONResponse = null;
        geoJSONResponse = searchFn(
                "API_울산_시경계",
                null,
                null
        );

        if (geoJSONResponse != null) {
            resultList.add(geoJSONResponse);
        }

        return resultList;
    }

    // GIS 검색 (구군) 사용X
    public ArrayList<GeoJSONResponse> searchGugun() throws JsonProcessingException {
        ArrayList<GeoJSONResponse> resultList = new ArrayList<>();

        for (String gugun : gugunList) {
            GeoJSONResponse geoJSONResponse = null;
            geoJSONResponse = searchFn(
                    "2021_" + gugun + "_구경계",
                    null,
                    null
            );

            if (geoJSONResponse == null) {
                continue;
            }

            resultList.add(geoJSONResponse);
        }

        return resultList;
    }

    // GIS 검색 (법정동) 사용X
    public ArrayList<GeoJSONResponse> searchDong() throws JsonProcessingException {
        ArrayList<GeoJSONResponse> resultList = new ArrayList<>();

        for (String gugun : gugunList) {
            GeoJSONResponse geoJSONResponse = null;
            geoJSONResponse = searchFn(
                    "2021_" + gugun + "_법정동",
                    null,
                    null
            );

            if (geoJSONResponse == null) {
                continue;
            }

            resultList.add(geoJSONResponse);
        }

        return resultList;
    }

    // GIS 검색 (지적도) 사용X
    public byte[] searchStreet(String level, String gugun, String bbox, String srs) throws JsonProcessingException {
        Mono<byte[]> resource = null;

        resource = searchSvgFn(
                "API_" + gugun + "_지적도",
                bbox,
                srs,
                1000,
                1000
        );

        byte[] imageByte = resource.block();

        return imageByte;
    }

    // 성과품 SHP 가져오기
    public List<ShpResultDto.ShpResultRes> getShp2() {
        List<ShpResult> resultList = shpResultRepository.findAll();

        List<ShpResultDto.ShpResultRes> shpResultRes = resultList.stream()
                .map(ShpResult::toShpResultRes)
                .collect(Collectors.toList());

        return shpResultRes;
    }

    // 검색 함수 (geoJson)
    private GeoJSONResponse searchFn(String table, String bbox, String filter) throws JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(GEOSERVER_DOMAIN + "/geoserver/" + GEOSERVER_WORKSPACE + "/ows")
                .build();

        GeoJSONResponse geoJSONResponse = null;
        try {
            String response = webClient
                    .get()
                    .uri(uriBuilder -> {
                        // 베이스
                        uriBuilder
                                .queryParam("service", "WFS")
                                .queryParam("version", "1.0.0")
                                .queryParam("request", "GetFeature")
                                .queryParam("typename", table)
                                .queryParam("outputFormat", "application/json")
                                .queryParam("srsname", "EPSG:3857");
                        if (bbox != null) {
                            uriBuilder
                                    .queryParam("bbox", bbox);

                        } else if (filter != null) {
                            uriBuilder
                                    .queryParam("CQL_FILTER", filter);
                        }

                        return uriBuilder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            geoJSONResponse = objectMapper.readValue(response, GeoJSONResponse.class);
        } catch (JsonProcessingException e) {
            log.error("geoserver 레이어 조회 실패 에러: {}, {}, {}", table, bbox, filter);

            return null;
        }

        return geoJSONResponse;
    }

    // 검색 함수 (svg) 사용X
    Mono<byte[]> searchSvgFn(String table, String bbox, String srs, int width, int height) throws JsonProcessingException {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // 무제한
                .build();

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(GEOSERVER_DOMAIN + "/geoserver/" + GEOSERVER_WORKSPACE + "/wms")
                .build();

        Mono<byte[]> svgResourceMono = null;
        try {
            svgResourceMono = webClient
                    .get()
                    .uri(uriBuilder -> {
                        // 베이스
                        uriBuilder
                                .queryParam("service", "WMS")
                                .queryParam("version", "1.0.0")
                                .queryParam("request", "GetMap")
                                .queryParam("layers",  GEOSERVER_WORKSPACE + ":" + table)
                                .queryParam("TRANSPARENT",true)
                                .queryParam("bbox", bbox)
                                .queryParam("srs", srs)
                                .queryParam("width", width)
                                .queryParam("height", height)
                                .queryParam("format", "image/png"); // SVG 형식으로 요청

                        URI build = uriBuilder.build();

                        return build;
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(byte[].class);

        } catch (Exception e) {
            log.error("geoserver 레이어 조회 실패 에러: {}, {}, {}", table, bbox, srs);
            return null;
        }


        return svgResourceMono;
    }

    // geoserver 레이어 정보 가져오기 (SRS, BBOX)
    public HashMap<String,String> getLayerInfo(String layerName) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        HashMap<String, String> result = new HashMap<>();

        // GeoServer GetCapabilities URL
        String getCapabilitiesUrl = GEOSERVER_DOMAIN + "/geoserver/+ " + GEOSERVER_WORKSPACE + "/wms?service=WMS&version=1.1.0&request=GetCapabilities";

        // Create a WebClient
        WebClient webClient = WebClient.create();

        // Send a GET request to fetch the GetCapabilities XML
        String xmlResponse = webClient.get()
                .uri(getCapabilitiesUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlResponse));
        Document doc = builder.parse(inputSource);

        // Create an XPath object
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // XPath expression to select the layer with the specified name
        String layerXPath = String.format("//Layer[Name='%s']", layerName);

        // Compile the XPath expression
        XPathExpression expr = xpath.compile(layerXPath);

        // Execute the XPath expression to get the specific layer node
        Element layerNode = (Element) expr.evaluate(doc, XPathConstants.NODE);
        NodeList boundingBoxList = layerNode.getElementsByTagName("BoundingBox");
        Element boundingBox = (Element) boundingBoxList.item(0);

        if (layerNode != null) {
            // Extract SRS and BBOX information from the layer node
            String srs = xpath.evaluate("SRS", layerNode);

            String minx = boundingBox.getAttribute("minx");
            String miny = boundingBox.getAttribute("miny");
            String maxx = boundingBox.getAttribute("maxx");
            String maxy = boundingBox.getAttribute("maxy");

            result.put("SRS", srs);
            result.put("BBOX", minx + "," + miny + "," + maxx + "," + maxy);
            // Extract the feature type based on geometry type
            String geometryType = xpath.evaluate("Layer[Name='" + layerName + "']/Style/FeatureType/GeometryType", doc);

            result.put("FeatureType", geometryType);

        } else {
            System.out.println("Layer not found: " + layerName);
        }

        return result;
    }

    // 성과품 SHP 정보 저장
    @Transactional
    public ShpResultDto.ShpResultRes insertShpResult(ShpResultDto.ShpResultReq resultReq, boolean retry) {
        List<MultipartFile> files = resultReq.getFiles();
//        String tablePath = resultReq.getTableName();
        List<FileInfo> currentRes = null;

        // 재등록이면 기존 데이터 삭제
        if (retry) {
            shpResultRepository.deleteById(resultReq.getResultNo());
        }

        // SHP 정보 중복검사
        if (resultReq.getSubType().equals("블럭경계") && resultReq.getCardYn().equals("Y")) {
            ShpResult dbData2 = shpResultRepository.findBySubTypeAndCardYnAndYearAndRegCode(
                    resultReq.getSubType(),
                    resultReq.getCardYn(),
                    resultReq.getYear(),
                    resultReq.getRegCode()
            );
            if (dbData2 != null) throw new ShpInsertException(MyUtil.getEnum(Msg.ALREADY_EXISTS, "SHP"));
        }

        // 2) 파일 유효성 검사
        Boolean validation = validation4SHP(files);
        if (!validation) throw new ShpInsertChkException();
        log.info("--- 2) 파일 유효성 검사 완료");

        // 1) 기본 SHP 데이터 DB에 저장
        ShpResult shpResult = shpResultService.save(resultReq);
        String tablePath = "";
        tablePath += "SHP_RESULT_";
        tablePath += shpResult.getResultNo();
        shpResult.updateTableName(tablePath);
        log.info("--- 1) SHP 정보 저장 완료");

        //        3) 첨부파일 있으면 같이 업로드
        if (files != null && !files.isEmpty()) {
            List<FileManager.Res> detailList = fileManager.saveShpFiles(files, tablePath);
            List<FileInfo> attaches = new ArrayList<>();

            for (FileManager.Res detail : detailList) {
                attaches.add(
                        fileInfoRepo.save(FileInfo.builder()
                                .fileNm(detail.getOriginNm())
                                .fileNmStored(detail.getSavedNm())
                                .filePath(detail.getPath())
                                .rschShp(shpResult)
                                .build()
                        )
                );
            }

//            4) 문서에 파일 연결. 트랜잭션(영속성 컨텍스트) 안에서 dirty check 는 save 순서에 상관없이 적용.
            shpResult.addAttaches(attaches);
        }

        ShpResult save = shpResultRepository.save(shpResult);

        return save.toShpResultRes();
    }

    @Transactional
    public void shpResultChange(Long resultNo, Integer status) throws IOException {
        ShpResult shpResult = shpResultRepository.findById(resultNo).get();
        shpResult.updateState(status,"");
        shpResultRepository.saveAndFlush(shpResult);
    }

    /**
     * 사용자가 shapefile 을 입력하고 서버 저장소를 거쳐 postgres 테이블에 생성되기까지 과정
     * @param resultNo SHP 아이디
     * @return fileRes
     * @throws IOException 아마 테이블명 중복 에러
     * @throws FactoryException postgres 접속 실패?
     */
    @Transactional
    public void insertResultShpToPostgres(Long resultNo) throws IOException {
        ShpResult shpResult = shpResultRepository.findById(resultNo).get();
        String tablePath = shpResult.getTableName();
        String epsg = shpResult.getEpsg();
        FileInfo shpFile = null;
        FileInfo cpgFile = null;
        FileInfo dbfFile = null;
        String fileNm = null;

        for (FileInfo fileInfo : shpResult.getAttaches()) {
            String ext = MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false);

            if (ext.equals(".shp")) {
                shpFile = fileInfo;
                fileNm = fileInfo.getFileNm().substring(0, fileInfo.getFileNm().lastIndexOf("."));
            }

            if (ext.equals(".dbf")) {
                dbfFile = fileInfo;
            }
        }

        try {
            // 4) postgresql에 shp 저장
            boolean result = saveShp2Postgis(fileNm, shpResult, shpFile, dbfFile, tablePath);
            if (!result) throw new ShpInsertException();
            log.info("--- 4) postgre 테이블 생성 완료");

            /*
            .dbf 가 너무 크면 geotools 는 성능 저하 발생 -> 서버 명령어로 덤프 인서트 방식 채택.
            그러나 docker 의 shp2pgsql 적용 범위 때문에 운영 서버 안이 아니면 테스트 곤란.
            */
            // 4) jsch 방식: postgresql 저장 > 테이블 생성.
            // shp2pgsql(fileNm);

            // 5) 레이어 발행
            publishLayer(tablePath, epsg, fileNm);
            log.info("--- 5) 레이어 발행 완료 {}", shpResult.getName());

            shpResult.updateState(2,"");
            shpResultRepository.save(shpResult);

        } catch (ShpInsertException e) {
            System.out.println(e.getMessage());
            shpResult.updateState(-1, "업로드 도중 문제가 발생했습니다. ShpInsertException");
            shpResultRepository.save(shpResult);
            deleteShp2Postgres(shpResult.getResultNo(), false);

        } catch (IOException e) {
            shpResult.updateState(-1, "업로드 도중 문제가 발생했습니다. IOException");
            shpResultRepository.save(shpResult);
            deleteShp2Postgres(shpResult.getResultNo(), false);

        } catch (FactoryException e) {
            shpResult.updateState(-1, "업로드 도중 문제가 발생했습니다. FactoryException");
            shpResultRepository.save(shpResult);
            deleteShp2Postgres(shpResult.getResultNo(), false);

        } catch (Exception e) {
            shpResult.updateState(-1, "업로드 도중 문제가 발생했습니다. Exception");
            shpResultRepository.save(shpResult);
            deleteShp2Postgres(shpResult.getResultNo(), false);
        }
    }

    // 레이어 발행 pre 함수
    @Transactional
    public void publishLayer(String tablePath, String epsg, String fileNm) throws IOException {
        try {
            deleteFeature(tablePath);
        } catch (WebClientResponseException e) {}

        Map<String, String> bounds = getNativeBoundingBox(fileNm, tablePath);

        String epsgCode = "";

        if (epsg.isEmpty()) {
            epsgCode = "EPSG:5187";

            // 사용자가 등록한 좌표계 사용
        } else {
            epsgCode = "EPSG:" + epsg;
        }

        publishNewFeatureType(tablePath,epsgCode,bounds);
    }

    // 레이어 발행 pre 함수 (등록된 SHP 정보를 row를 기반으로)
    @Transactional
    public void publishLayer(Long resultNo) throws IOException {
        ShpResult shpResult = shpResultRepository.findById(resultNo).get();
        String tablePath = shpResult.getTableName();
        String epsg = shpResult.getEpsg();

        FileInfo shpFile = null;
        String fileNm = null;

        for (FileInfo fileInfo : shpResult.getAttaches()) {
            String ext = MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false);

            if (ext.equals(".shp")) {
                shpFile = fileInfo;
                fileNm = fileInfo.getFileNm().substring(0, fileInfo.getFileNm().lastIndexOf("."));
            }
        }

        try {
            deleteFeature(tablePath);
        } catch (WebClientResponseException e) {}

        Map<String, String> bounds = getNativeBoundingBox(fileNm, tablePath);

        // prj 파일이 있으면 좌표계 조회
        String epsgCode = null;

        if (epsgCode == null) {
            // 기본 좌표계 사용 (성과품)
            if (epsg.isEmpty()) {
                epsgCode = "EPSG:5187";

                // 사용자가 등록한 좌표계 사용
            } else {
                epsgCode = "EPSG:" + epsg;
            }
        }

        publishNewFeatureType(tablePath,epsgCode,bounds);
    }

    // 성과품 SHP 정보 수정 (레이어, 디비 등은 미포함)
    @Transactional
    public ShpResultDto.ShpResultRes patchShp2Postgres(ShpResultDto.ShpResultReq resultReq) {
        Optional<ShpResult> shpResultOptional = shpResultRepository.findById(resultReq.getResultNo());

        ShpResult shpResult = shpResultOptional.get();

        if (shpResult.getSubType().equals("블럭경계") && resultReq.getCardYn().equals("Y")) {
            ShpResult dbData2 = shpResultRepository.findBySubTypeAndCardYnAndYearAndRegCode(
                    shpResult.getSubType(),
                    resultReq.getCardYn(),
                    shpResult.getYear(),
                    shpResult.getRegCode()
            );
            if (dbData2 != null) throw new ShpInsertException("이미 관리카드 탭에서 사용 중인 성과품이 있습니다.");
        }

        shpResult.update(resultReq);

        Optional<Code> codeOptional = codeRepository.findByName(resultReq.getRegCode());
        if (codeOptional.isPresent()) {
            shpResult.updateRegCode(codeOptional.get());
        }

        ShpResult save = shpResultRepository.save(shpResult);

        return save.toShpResultRes();
    }

    // 성과품 SHP 삭제 (레이어, 파일 DB, 파일 저장소, SHP 정보 row, postgre 테이블)
    // rowDelete가 true일 경우에만 SHP 정보 삭제
    @Transactional
    public void deleteShp2Postgres(Long resultNo, boolean rowDelete) throws IOException {
        // shp 정보 가져오기
        Optional<ShpResult> shpData = shpResultRepository.findById(resultNo);

        if (shpData.isPresent()) {
            ShpResult shpResult = shpData.get();
            List<FileInfo> attaches = shpResult.getAttaches();
            String tableName = shpResult.getTableName();

            // 레이어 삭제
            try {
                deleteFeature(tableName);
                log.info("---1 레이어 삭제 완료: {}", tableName);
            } catch (WebClientResponseException e) {
                log.info("---1 이미 삭제된 레이어입니다");
            }

            // postgre 테이블 삭제
            Map<String, Object> params = connectPostgreDb();
            DataStore dataStore = null;

            try {
                dataStore = DataStoreFinder.getDataStore(params);
                dataStore.removeSchema(tableName);
                log.info("---4 postgre 테이블 삭제 완료: {}", tableName);

            } catch (IOException e) {
                log.info("디비접속 중 문제가 발생했습니다.");

            } catch (IllegalArgumentException e) {
                log.info("테이블이 없습니다.");
            }
            finally {
                dataStore.dispose();
            }

            // shp_result 삭제
            if (rowDelete) {
                shpResultRepository.deleteById(shpResult.getResultNo());
                log.info("---5 SHP_RESULT 삭제 완료");
            }

            // 디렉토리 삭제
            File directory = new File(shpResult.getAttaches().get(0).getFilePath());
            if (directory.exists()) {
                fileService.deleteDirectory(directory);
            }
            log.info("---3 디렉토리 삭제 완료: {}", tableName);

        } else {
            log.info("이미 삭제된 SHP입니다.");
        }
    }

    /**
     * shapefile 유효성 검사
     *
     * @param files shapefiles
     * @return Pass or Fail
     */
    public Boolean validation4SHP(List<MultipartFile> files) {
//        1-1) 최소 파일 개수를 충족하는가?
        String[] requiredArray = {".shp", ".shx", ".dbf"};
        if (files == null || files.size() < requiredArray.length) return false;

        // 확장자 없는 파일명 추출
//        String fileNm = files.get(0).getOriginalFilename()
//                .substring(0, files.get(0).getOriginalFilename().lastIndexOf("."));
        String fileNm = MyUtil.getFileNmOrExt(files.get(0).getOriginalFilename(), true);

//        1-2) 파일명이 모두 일치하는가?
        List<String> extensions = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            // 매개값 타입이 MultipartFile 인 경우 반드시 getOriginalFilename() 사용.
//            if (!multipartFile.getOriginalFilename().substring(0, multipartFile.getOriginalFilename().lastIndexOf("."))
//                    .equals(fileNm))

            if (!(MyUtil.getFileNmOrExt(multipartFile.getOriginalFilename(), true)).equals(fileNm)) {
                System.out.println("File names do not matched. Check and try again.");
                return false;
            } else {
                //확장자만 썰어서 비교.
//                extensions.add(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")));
//                extensions.add(getExtenstion(multipartFile));
                extensions.add(MyUtil.getFileNmOrExt(multipartFile.getOriginalFilename(), false));
            }
        }

//        1-3) 필요한 파일이 모두 있는가?
        for (String s : requiredArray) {
            if (!extensions.contains(s)) {
                System.out.println(s + " does not exist. Check and try again.");
                return false;
            }
        }

//        1-4)  shapefile 아닌 파일이 섞이진 않았는가?
        if (!Arrays.asList(SHP_FILE).containsAll(extensions)) {
            System.out.println("It doesn't seem to be a shapefile. Remove unnecessary files.");
            return false;
        }

//        1-5) db 내용과 비교해 파일명 중복이 없는가?
//        List<FileInfo> list = fileInfoRepo.findAll();
//        for (int i = 0; i < list.size(); i++) {
//            if (files.get(0).getOriginalFilename().substring(0, files.get(0).getOriginalFilename().lastIndexOf("."))
//                    .equals(list.get(i).getFile_nm().substring(0, list.get(i).getFile_nm().lastIndexOf(".")))) {
//                System.out.println("This filename already exists.");
//                return false;
//            }
//        }
        // [변경] 저장소를 분리하면 db 기록만으로 중복을 알 수 없으므로 저장소 탐색 중복 검사.
//        File[] shpDir = new File(fileDirSHP + tablePath + "/").listFiles();
//
//        assert shpDir != null;
//
//        if (shpDir == null) {
//            System.out.println("These files already exists. Try again.");
//            return true;
//        }
//
//        for (File file : shpDir) {
//            String fileNmNoExt = MyUtil.getFileNmOrExt(file.getName(), true);
//            if (fileNmNoExt.equals(fileNm)) {
//                System.out.println("These files already exists. Try again.");
//                return false;
//            }
//        }

//        1-6) (저장소엔 중복이 없더라도) 이미 같은 이름의 테이블이 존재하는가?
        // db 접속
//        Map<String, Object> params = connectPostgreDb();
//
//        DataStore dataStore = null;     // db 발견
//        try {
//            dataStore = DataStoreFinder.getDataStore(params);
//            String[] typeNames = dataStore.getTypeNames();
//
//            if (Arrays.stream(typeNames).collect(Collectors.toList()).contains(tablePath)) {
//                System.out.println("The table already exists. Batch rename the files and try again.");
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();        // db 접속 불량일 경우 IOException
//        } finally {
//            assert dataStore != null;
//            dataStore.dispose();
//        }

        return true;
    }

    @Async
    public void asyncAddFeatures(SimpleFeatureStore store, SimpleFeatureCollection features) throws IOException {
        List<FeatureId> featureIds = store.addFeatures(features);

        for (FeatureId featureId : featureIds) {
            log.info(featureId.getID());
        }
    }

    /**
     * 라이브러리 사용: postgres 에 테이블 생성
     *
     * @param fileNm      서버 내에 존재하는 셰이프파일명 -> 테이블명
     * @param shpResult
     * @return success or not
     */
    @Transactional
    public boolean saveShp2Postgis(String fileNm, ShpResult shpResult, FileInfo shpFile, FileInfo dbfFile, String tablePath) throws IOException, FactoryException {
        String shapeFileLoc = shpFile.getFilePath() + shpFile.getFileNm();
        String dbfFileLoc = dbfFile.getFilePath() + dbfFile.getFileNm();

//       ) read shapefile to save
        // medium shp
        URL url = new File(shapeFileLoc).toURI().toURL();
        ShapefileDataStore shpDataStore = new ShapefileDataStore(url);
        shpDataStore.setCharset(Charset.forName("EUC-KR"));

        /*
        setCharset()은 디코딩입니다. getCharset()으로 파일의 인코딩을 읽으면 ISO-8895-1.
        .cpg 가 없으면 구체적인 인코딩 정보(euc-kr or utf-8)를 읽어낼 수 없습니다.
        일반적으로 shapefile 은 euc-kr 를 사용한다는데 최근엔 utf-8로 바뀌는 추세라 대안이 필요합니다.
         */
//        if (cpgFile != null) {
//            try {
//                File cpg = new File(cpgFile.getFilePath() + cpgFile.getFileNm());
//                if (cpg.exists()) {
//                    FileInputStream fis = new FileInputStream(cpg);
//
//                    byte[] buf = new byte[10];
//                    fis.read(buf);
//                    String charset = new String(buf);    // specified charset
//
//                    fis.close();
//                    if (charset.startsWith("UTF-8")) {
//                        shpDataStore.setCharset(StandardCharsets.UTF_8);
//                    } else {
//                        shpDataStore.setCharset(Charset.forName("euc-kr"));
//                    }
//                } else {
//                    shpDataStore.setCharset(Charset.forName("euc-kr"));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        2) extract features from shp
        SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(fileNm);
        SimpleFeatureCollection features = featureSource.getFeatures();

//        3) set builder for table (table name, columns, crs...)
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(tablePath);        // typeName.equals(fileNm) == true
        builder.setAttributes(features.getSchema().getAttributeDescriptors());

        // TODO: 레이어 발행은 사용자 입력에 따라 달라지는데 디비 생성은 고정?? -- 상관없는 걸로 보임
//        builder.setCRS(CRS.decode("EPSG:5187"));
        builder.setCRS(DefaultGeographicCRS.WGS84);

        // make schema
        SimpleFeatureType newSchema = builder.buildFeatureType();

//        4) connect db
        Map<String, Object> params = connectPostgreDb();

        // medium db
        DataStore dataStore = DataStoreFinder.getDataStore(params);
        // create table
        dataStore.createSchema(newSchema);

//        5) insert into [newSchema] values ()
        SimpleFeatureStore store = (SimpleFeatureStore) dataStore.getFeatureSource(tablePath);

        String typeName = newSchema.getGeometryDescriptor().getType().getName().toString();
        shpResult.updateType(typeName.toUpperCase());

        Transaction transaction = new DefaultTransaction("handler");
        try {
            store.setTransaction(transaction);
            store.addFeatures(features);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();

            e.printStackTrace();
            return false;
        } finally {
            transaction.close();

            shpDataStore.dispose();
            dataStore.dispose();

        }

        return true;
    }

    /**
     * 서버 명령어 사용: postgres 에 테이블 생성. [성능 우수]
     *
     * @param fileNm 테이블명* 명령어 구조상 영어는 소문자만 인식
     * @return success or not
     */
    // TODO: 라이브러리에 사용하는 로직이랑 일치하게 변경 필요!! (SHP 정보 row 갱신, 파일 위치 변경됨) 23.10.16 - 재진
    public boolean shp2pgsql(String fileNm) {
        // 접속 정보
        String host = "14.44.109.152";
        int port = 22;
        String user = "atech";
        String pw = "Dpdlxpzm1221!@";

        // 파일명에 대문자가 있는지 확인. shp2pgsql 사용 시 대문자를 소문자로 자동 변환해 저장하기 때문.
        // 일반적으로는 파일명을 작은 따옴표로 감싸지만, shp2pgsql 의 적용 범위에 들어가는 게 최우선인 이 명령어에서는 따옴표를 쓰기가 어렵습니다.
        if (fileNm.matches(".*[A-Z].*")) {
            System.out.println("Not an error, but the table name are stored in lower case.");
        }

        // 명령어. TODO: 운영 서버에서는 해당 디렉토리로 파일이 올라가도록 fileDirSHP 환경설정 분리 필요.
        String cmd = "docker exec postgis-db-1 sh -c \"shp2pgsql -D -s 5187 " + fileDirSHP + "'" + fileNm + ".shp' " + "`" + fileNm + "`" + " | psql -U atech -d postgres\"\n";

        Session session = null;
        ChannelExec exec = null;

//        1) 접속
        try {
            session = new JSch().getSession(user, host, port);
            session.setPassword(pw);
            java.util.Properties config = new java.util.Properties();
            // 호스트 정보를 검사하지 않도록 설정
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

//           2) 명령어 실행
            Channel channel = session.openChannel("exec");
            exec = (ChannelExec) channel;

            exec.setCommand(cmd);
            exec.connect(); //명령 실행

////           3) 결과 수신 - 옵션.
//            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
//            StringBuffer sb = new StringBuffer();
//
//            String line = null;     // 형태 변경 가능.
//            while ((line = reader.readLine()) != null)
//            {
//                sb.append(line+" ");
//            }
//            System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            접속 끊기. 연결 해제.
            if (session != null) session.disconnect();
            if (exec != null) exec.disconnect();
        }

        return true;
    }

    /**
     * 원본 데이터의 최소 경계 영역 추출.
     *
     * @param fileNm file name in server directory
     * @return minx, miny, maxx, maxy
     */
    public Map<String, String> getNativeBoundingBox(String fileNm, String tablePath) {
        Map<String, String> bounds = new HashMap<>();

        try {
            URL url = new File(fileDirSHP + tablePath + File.separator + fileNm + ".shp").toURI().toURL();

            // shapefileDataStore로 해당 파일의 스키마 정보에 접근
            ShapefileDataStore sds = new ShapefileDataStore(url);
            SimpleFeatureCollection shpInfo = sds.getFeatureSource(sds.getTypeNames()[0]).getFeatures();

            // 원본 데이터의 최소 경계 영역 추출.
            bounds.put("minx", String.valueOf(shpInfo.getBounds().getMinX()));
            bounds.put("miny", String.valueOf(shpInfo.getBounds().getMinY()));
            bounds.put("maxx", String.valueOf(shpInfo.getBounds().getMaxX()));
            bounds.put("maxy", String.valueOf(shpInfo.getBounds().getMaxY()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bounds;
    }


    /**
     * geoserver 에 레이어 발행
     *
     * @param tableNm postgres 에 존재하는 테이블명
     * @param bounds  min, max boundary values
     */
    public void publishNewFeatureType(String tableNm, String epsgCode, Map<String, String> bounds) throws IOException {

        /*
        GeoServer 계정 바뀌면 api 고장남
         */
        String id = GEOSERVER_USER;
        String pw = GEOSERVER_PASS;
        String EPSG = epsgCode;

//        1) body.
        String xml = "<featureType>\n" +
                "<name>" + tableNm + "</name>\n" +
                "<nativeName>" + tableNm + "</nativeName>\n" +
                "<namespace>\n" +
                "<name>" + GEOSERVER_WORKSPACE + "</name>\n" +
                "</namespace>\n" +
                "<title>" + tableNm + "</title>\n" +
                "<keywords>\n" +
                "<string>features</string>\n" +
                "<string>" + tableNm + "</string>\n" +
                "</keywords>\n" +
                "<nativeCRS>GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]]</nativeCRS>\n" +
                "<srs>" + EPSG + "</srs>\n" +
                "<nativeBoundingBox>\n" +
                "<minx>" + bounds.get("minx") + "</minx>\n" +
                "<miny>" + bounds.get("miny") + "</miny>\n" +
                "<maxx>" + bounds.get("maxx") + "</maxx>\n" +
                "<maxy>" + bounds.get("maxy") + "</maxy>\n" +
                "<crs>EPSG:" + EPSG + "</crs>\n" +
                "</nativeBoundingBox>\n" +
                "</featureType>";

        JSONObject jsonParam = XML.toJSONObject(xml);

//      2)  request. TODO: 적절한 res 를 어떻게 받을지.
        WebClient webClient = WebClient.create();
        webClient.post()
                .uri(GEOSERVER_DOMAIN + "/geoserver/rest/workspaces/" + GEOSERVER_WORKSPACE + "/featuretypes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(id, pw))
                .bodyValue(jsonParam.toString())
                .retrieve()
                .bodyToMono(String.class)
//                .doOnSuccess(res -> System.out.println("res: " + res))
                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    throw new ShpInsertException("레이어 발행에 실패했습니다.");
                })
                .block();
    }

    // geoserver 레이어 삭제
    public void deleteFeature(String feature) {
        String id = GEOSERVER_USER;
        String pw = GEOSERVER_PASS;
        WebClient webClient = WebClient.create();
        String block = webClient.delete()
                .uri(GEOSERVER_DOMAIN + "/geoserver/rest/workspaces/" + GEOSERVER_WORKSPACE + "/featuretypes/" + feature + "?recurse=true")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(id, pw))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 구글 api 지오코딩
     * @param location 장소
     * @return 위경도
     */
    public Map<String, String> geoCoding_googleAPI(String location) {
//        sample
        String key = "AIzaSyACB3PXBawkx79Rm09982rvfqKhhZooZ8Y";

        String baseUrl = "https://maps.googleapis.com/maps/api/geocode";
        List<String> list = new ArrayList<>();

//        request
        WebClient webClient = WebClient.create(baseUrl);
        webClient.get()
                .uri("/json?address=" + location + "&key=" + key + "&language=ko")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(list::add)
//                .doOnSuccess(res -> System.out.println("res: "+res))
                .onErrorResume(Exception -> {
                    throw new RuntimeException("Error 500. Check url or param.");
                })
                .block();

//        parsing
        JSONObject jsonObject = new JSONObject(list.get(0));
        JSONObject loc = jsonObject.getJSONArray("results")
                .getJSONObject(0).getJSONObject("geometry")
                .getJSONObject("location");

        Map<String, String> latlng = new HashMap<>();
        latlng.put("lat", loc.get("lat").toString());
        latlng.put("lng", loc.get("lng").toString());

        return latlng;
    }


    /*
   shp 데이터를 png 로 변환, 파일 다운로드.
    */
    public FileInfoDto.FileInfoRes shp2Png_file(Long fileId){
//        now on test.
        String fileNm = "";
        Optional<FileInfo> fileInfoOptional = fileInfoRepo.findById(fileId);
        if(!fileInfoOptional.isPresent()){
            throw new EntityNotFoundException("No such file.");
        }

        String originalFileNm = fileInfoOptional.get().getFileNm();
        fileNm = originalFileNm.substring(0, originalFileNm.lastIndexOf("."));

        try {
//            1) extract features from .shp by ShapefileDataStore
            URL url = new File(fileDirSHP + File.separator + fileNm + ".shp").toURI().toURL();
            ShapefileDataStore ds = new ShapefileDataStore(url);
            ContentFeatureCollection features = ds.getFeatureSource(ds.getTypeNames()[0]).getFeatures();

//            2) render
            MapContent mapContent = new MapContent();
            mapContent.setTitle(fileNm);

            Style style = SLD.createSimpleStyle(features.getSchema());
            Layer layer = new FeatureLayer(features, style);
            mapContent.addLayer(layer);

//            3) convert to .png
            File output = new File(fileNm + ".png");
            FileOutputStream os = new FileOutputStream(output);
            ImageOutputStream osImg = ImageIO.createImageOutputStream(os);

            // 만들어질 이미지의 사이즈 지정
            int width = 1000;
            ReferencedEnvelope bounds = features.getBounds();
            int height = (int) (width * (bounds.getHeight() / bounds.getWidth()));

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bufferedImage.createGraphics();

            // 기타 그래픽 설정
            mapContent.getViewport().setMatchingAspectRatio(true);
            mapContent.getViewport().setScreenArea(new Rectangle(Math.round(width), Math.round(height)));
            mapContent.getViewport().setBounds(bounds);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Rectangle outputArea = new Rectangle(width, height);
            GTRenderer render = new StreamingRenderer();
            LabelCacheImpl labelCache = new LabelCacheImpl();

            Map<Object, Object> hints = render.getRendererHints();
            if(hints == null){
                hints = new HashMap<>();
            }

            hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
            render.setRendererHints(hints);
            render.setMapContent(mapContent);
            render.paint(graphics, outputArea, bounds);

//            4) write
            ImageIO.write(bufferedImage, "png", osImg);
            mapContent.dispose();       //쓰고 닫기.
            ds.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        5) response
        return fileInfoOptional.get().toFileInfoRes();
    }

    /**
     * read EPSG from .shp
     *
     * @param fileNm 같은 위치에 최소 3개 ~ 최대 6개 파일명이 모두 동일해야 함
     * @return 좌표계값(EPSG)
     */
    public String getEPSG(String fileNm) {
        String EPSG = "";
        try {
            URL url = new File(fileDirSHP + File.separator + fileNm + ".shp").toURI().toURL();

//            shapefileDataStore로 해당 파일의 스키마 정보에 접근
            ShapefileDataStore sds = new ShapefileDataStore(url);
            SimpleFeatureCollection shpInfo = sds.getFeatureSource(sds.getTypeNames()[0]).getFeatures();

            // .prj(CRS 정보가) 없는 경우
            if (shpInfo.getSchema().getCoordinateReferenceSystem() == null) {
                return EPSG;
            }

//            좌표계(CRS, Coordinate Reference System)
            Integer epsg = CRS.lookupEpsgCode(shpInfo.getSchema().getCoordinateReferenceSystem(), true);
            EPSG = epsg.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EPSG;
    }


    /**
     * get the number of record in shapefile by reading .dbf
     *
     * @param fileNm file name in server directory
     * @return int records
     */
    public int getRecords(String fileNm) {
        int records = 0;

        try {
            File dbf = new File(fileDirSHP + File.separator + fileNm + ".dbf");
            FileInputStream fis = new FileInputStream(dbf);

            // 디코딩이 안 맞더라도 레코드 개수를 구하는 데는 문제 없습니다.
            DbaseFileReader reader = new DbaseFileReader(fis.getChannel(), true, Charset.forName("euc-kr"));
            records = reader.getHeader().getNumRecords();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }


    /**
     * 임시파일 저장소에 실제 shapefile 이 존재하는지 검사
     *
     * @param fileNm 확장자 뗀 순수 파일명
     * @return 서버 임시파일 저장소에 필수 확장자가 모두 존재하는지 여부.
     */
    public Boolean checkSHPExist(String fileNm) {
        List<File> list = new ArrayList<>();
        File file1 = new File(tmpDir + File.separator + fileNm + ".shp");
        File file2 = new File(tmpDir + File.separator + fileNm + ".shx");
        File file3 = new File(tmpDir + File.separator + fileNm + ".dbf");

        list.add(file1);
        list.add(file2);
        list.add(file3);

//        필요한 파일이 전부 존재해야(true) 다음 단계 진행
        boolean isExist = true;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).exists()) {
                isExist = false;
                switch (i) {
                    case 0:
                        System.out.println("Make sure .shp does exist.");
                        break;
                    case 1:
                        System.out.println("Make sure .shx does exist.");
                        break;
                    case 2:
                        System.out.println("Make sure .dbf does exist.");
                        break;
                    default:
                        break;
                }
            }
        }
        return isExist;
    }

//    public int getBuildingManagement(List<BuildingManagementDto.BuildingManagementReq> req) {
//        final int[] sum = {0};
//
//        List<BuildingManagementDto.BuildingManagementReq> filteredList = req.stream()
//                .collect(Collectors.groupingBy(
//                        reqElement -> Arrays.asList(
//                                reqElement.getGuCode(),
//                                reqElement.getDongCode(),
//                                reqElement.getBunCode(),
//                                reqElement.getJiCode()
//                        )
//                ))
//                .values()
//                .stream()
//                .filter(group -> group.size() == 1) // Filter out groups with a single element
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
//
//        for (BuildingManagementDto.BuildingManagementReq buildingManagementReq : filteredList) {
//            List<BuildingManagement> managements = buildingManagementRepositoryCustom.search(
//                    buildingManagementReq.getGuCode(),
//                    buildingManagementReq.getDongCode(),
//                    buildingManagementReq.getBunCode(),
//                    buildingManagementReq.getJiCode()
//            );
//
//
//            if (managements.size() > 1) {
//                List<BuildingManagementAll> managementAlls = buildingManagementAllRepositoryCustom.search(
//                        buildingManagementReq.getGuCode(),
//                        buildingManagementReq.getDongCode(),
//                        buildingManagementReq.getBunCode(),
//                        buildingManagementReq.getJiCode()
//                );
//
//                if (managementAlls.size() != 0) {
//                    BuildingManagementAll managementAll = managementAlls.get(0);
//                    sum[0] += Integer.parseInt(managementAll.getINDR_AUTO_UTCNT());
//                    sum[0] += Integer.parseInt(managementAll.getINDR_MECH_UTCNT());
//                    sum[0] += Integer.parseInt(managementAll.getOUDR_AUTO_UTCNT());
//                    sum[0] += Integer.parseInt(managementAll.getOUDR_MECH_UTCNT());
//                }
//
//            } else {
//
//                if (managements.size() != 0) {
//                    BuildingManagement buildingManagement = managements.get(0);
//                    sum[0] += Integer.parseInt(buildingManagement.getINDR_AUTO_UTCNT());
//                    sum[0] += Integer.parseInt(buildingManagement.getINDR_MECH_UTCNT());
//                    sum[0] += Integer.parseInt(buildingManagement.getOUDR_AUTO_UTCNT());
//                    sum[0] += Integer.parseInt(buildingManagement.getOUDR_MECH_UTCNT());
//                }
//
//            }
//        }
//
//        return sum[0];
//    }
//
//    public int getBuildingManagement2(List<BuildingManagementDto.BuildingManagementReq> req) {
//        HashMap<String, BuildingManagementDto.BuildingManagementRes> managementHashMap = buildingManagementStore.getManagementHashMap();
//        HashMap<String, BuildingManagementDto.BuildingManagementRes> managementAllHashMap = buildingManagementStore.getManagementAllHashMap();
//        final int[] sum = {0};
//
//        for (BuildingManagementDto.BuildingManagementReq bmReq : req) {
//            String reqKey = bmReq.getGuCode() + bmReq.getDongCode() + bmReq.getBunCode() + bmReq.getJiCode();
//            BuildingManagementDto.BuildingManagementRes managementRes = null;
//
//            if (managementHashMap.containsKey(reqKey)) {
//                managementRes = managementHashMap.get(reqKey);
//
//            } else if (managementAllHashMap.containsKey(reqKey)) {
//                managementRes = managementAllHashMap.get(reqKey);
//
//            } else {
//                managementRes = null;
//            }
//
//            if (managementRes != null) {
//                sum[0] += Integer.parseInt(managementRes.getINDR_AUTO_UTCNT());
//                sum[0] += Integer.parseInt(managementRes.getINDR_MECH_UTCNT());
//                sum[0] += Integer.parseInt(managementRes.getOUDR_AUTO_UTCNT());
//                sum[0] += Integer.parseInt(managementRes.getOUDR_MECH_UTCNT());
//            }
//        }
//
//        return sum[0];
//    }

    // 공롱 레이어 가져오기
    public ArrayList<GisDto.CommonLayer> getCommonLayer(String sgg) throws IOException {

        // 시군구 베이스 레이어
        HashMap<String, Object> sigunguData = null;
        try {
            FileSystemResource resource = new FileSystemResource(jsonDir + "ulsanGu.json");
            byte[] jsonData = Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));
            String jsonContent = new String(jsonData);
            ObjectMapper objectMapper = new ObjectMapper();
            sigunguData = objectMapper.readValue(jsonContent, HashMap.class);
        } catch (IOException e) {
            sigunguData = null;
        }

        ArrayList<GisDto.CommonLayer> commonList = new ArrayList<>();
        commonList.add(new GisDto.CommonLayer("ulsanGu", "시군구", sigunguData, "base", "none"));
        commonList.add(new GisDto.CommonLayer("safe","보호구역", protectedAreaApiService.getData() ,"data", "3"));
        commonList.add(new GisDto.CommonLayer("fire","소방시설", fireHydrantApiService.getData() ,"data", "3"));
        commonList.add(new GisDto.CommonLayer("resident","거주자우선 주차장", residentXyService.getAll() ,"data", "1"));
        commonList.add(new GisDto.CommonLayer("subPk","부설 주차장", buildingManagementRepository.findBySigunguCdAndLonIsNotNull(Integer.parseInt(sgg)) ,"data", "1"));
        commonList.add(new GisDto.CommonLayer("fixCctv","고정식 CCTV", illFixedService.selectList(sgg) ,"data", "2"));

        PPublicDto.Keyword pubKeyword = new PPublicDto.Keyword();
        pubKeyword.setSggCd(sgg);
        pubKeyword.setMonth("");
        pubKeyword.setYear("");
        commonList.add(new GisDto.CommonLayer("prkplce","공영 주차장", pPublicRepoCustom.searchLast(pubKeyword) ,"data", "1"));

        // 표준데이터 셋
        StandardSetDto.Keyword standardKeyword = new StandardSetDto.Keyword();
        standardKeyword.setSggCd(sgg);
        standardKeyword.setMonth("");
        standardKeyword.setYear("");

//        List<StandardSetDto> standardSetDtos = standardMngRepoCustom.search(standardKeyword);
        List<StandardSetDto> standardSetDtos = stdMngService.selectStandardSet(standardKeyword);

        if (standardSetDtos != null && standardSetDtos.size() != 0) {
            ArrayList<GisDto.StandardData> standardDataArrayList = new ArrayList<>();
//            standardDataArrayList.add(new GisDto.StandardData("1,2,3","공영 주차장"));
            standardDataArrayList.add(new GisDto.StandardData("4,5,6","민영 주차장"));
//            standardDataArrayList.add(new GisDto.StandardData("7","부설 주차장"));
            standardDataArrayList.add(new GisDto.StandardData("8","부설개방 주차장"));
            standardDataArrayList.add(new GisDto.StandardData("9","사유지개방 주차장"));

            standardDataArrayList.stream().forEach(standardData -> {
                List<StandardSetDto> pubRdData = standardSetDtos.stream()
                        .filter(standardSetDto -> {
                            String[] splitTypes = standardData.getNo().split(",");
                            int count = 0;
                            for (String splitType : splitTypes) {
                                if (standardSetDto.getLotType().equals(splitType)) {
                                    count++;
                                }
                            }

                            if (count > 0) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .filter(standardSetDto -> standardSetDto.getLon() != null && standardSetDto.getLat() != null)
                        .collect(Collectors.toList());

                commonList.add(new GisDto.CommonLayer("standard" + standardData.getNo(),standardData.getName(), pubRdData ,"data", "1"));
            });
        }

        return commonList;
    }

    // 주차장 현황 데이터 가져오기
    public String getCurrentPkLayer() throws IOException {
        FileSystemResource resource = new FileSystemResource(jsonDir + "prkplceOpenJson.json");
        byte[] jsonData = Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));

        // 읽은 JSON 데이터를 문자열로 변환
        String jsonContent = new String(jsonData);

        return jsonContent;
    }
}
