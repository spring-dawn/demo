package com.example.demo.controller.gis;

import com.example.demo.domain.data.research.shp.ShpResultRepositoryCustom;
import com.example.demo.dto.GeoJSONResponse;
import com.example.demo.dto.GisDto;
import com.example.demo.dto.data.research.ShpResultDto;
import com.example.demo.service.GisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.opengis.referencing.FactoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RequiredArgsConstructor
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/gis",
        produces = "application/json"
)
public class GisController {
    private final GisService gisService;
    private final ShpResultRepositoryCustom query;

    // 검색 (디테일) - 미정
    @GetMapping("/search-detail")
    ResponseEntity<?> selectDetailList(GisDto.SearchReq searchReq) throws JsonProcessingException {
        HashMap<String, Object> resultMap = new HashMap<>();

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    // 검색 (object) (Shp 데이터)
    @PostMapping("/search-object")
    ResponseEntity<?> selectObjectList(
            @RequestBody List<Map<String, String>> searchTree,
            @RequestParam(name = "dong", defaultValue = "") String dong
    ) throws JsonProcessingException {
        HashMap<String, Object> resultMap = new HashMap<>();

        ArrayList<HashMap<String, Object>> objectList = gisService.searchObject(searchTree,dong);

        resultMap.put("data", objectList);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    // 검색 (경계선-geojson) (Shp 데이터)
    @GetMapping("/search-base")
    ResponseEntity<?> selectBaseList(@RequestParam("level") String level) throws JsonProcessingException {
        HashMap<String, Object> resultMap = new HashMap<>();

        if (level.equals("sido")) {
            ArrayList<GeoJSONResponse> geoJSONResponses = gisService.searchSido();
            resultMap.put("data", geoJSONResponses);

        } else if (level.equals("gungu")) {
            ArrayList<GeoJSONResponse> geoJSONResponses = gisService.searchGugun();
            resultMap.put("data", geoJSONResponses);

        } else if (level.equals("dong")) {
            ArrayList<GeoJSONResponse> geoJSONResponses = gisService.searchDong();
            resultMap.put("data", geoJSONResponses);

        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    // 검색 (경계선-png) (Shp 데이터)
    @GetMapping(value = "/search-base-svg", produces = MediaType.IMAGE_PNG_VALUE)
    ResponseEntity<byte[]> selectSvgBaseList(
            @RequestParam("level") String level,
            @RequestParam("gugun") String gugun,
            @RequestParam("bbox") String bbox,
            @RequestParam("srs") String srs
    ) throws JsonProcessingException {
        byte[] svgResource = gisService.searchStreet(level,gugun,bbox,srs);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=image.png")
                .body(svgResource);
    }

    // 레이어 기본 정보 조회 (Shp 데이터)
    @GetMapping("/shp-info")
    ResponseEntity<?> selectDetailList( @RequestParam("layer") String layer) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        HashMap<String, String> layerInfo = gisService.getLayerInfo(layer);

        return new ResponseEntity<>(layerInfo, HttpStatus.OK);
    }

    // 성과품 SHP 저장 (Shp 데이터)
    @PostMapping("/shp")
    public void insertShp2Postgres(@RequestParam Long resultNo) throws IOException, FactoryException {
        gisService.shpResultChange(resultNo, 1);
        gisService.insertResultShpToPostgres(resultNo);
    }

    // 성과품 SHP 저장 (board 데이터)
    @PostMapping("/shp-result")
    public ResponseEntity<?> insertShpResult(
            ShpResultDto.ShpResultReq resultReq,
            @RequestParam boolean retry
    ) {
        ShpResultDto.ShpResultRes shpResultRes = gisService.insertShpResult(resultReq, retry);

        return new ResponseEntity<>(shpResultRes, HttpStatus.OK);
    }

    // 성과품 SHP 가져오기 (board 데이터)
    @GetMapping("/shp")
    public ResponseEntity<?> getShp2Postgres() {
        List<ShpResultDto.ShpResultRes> shp2 = gisService.getShp2();

        return new ResponseEntity<>(shp2, HttpStatus.OK);
    }

    // 성과품 SHP 가져오기 (검색) (board 데이터)
    @GetMapping("/search")
    public ResponseEntity<?> search(ShpResultDto.ShpSearchReq req) {
        List<ShpResultDto.ShpResultRes> res = query.searchShp(req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 성과품 SHP 업데이트 (board 데이터)
    @PatchMapping(value = "/shp", produces = "application/json")
    public ResponseEntity<?> patchShp2Postgres(@RequestBody ShpResultDto.ShpResultReq resultReq) {
        ShpResultDto.ShpResultRes result = gisService.patchShp2Postgres(resultReq);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 성과품 SHP 삭제 (board 데이터, Shp 데이터)
    @DeleteMapping("/shp")
    public ResponseEntity<?> deleteShp2Postgres(@RequestParam Long resultNo) throws IOException {
        gisService.deleteShp2Postgres(resultNo, true);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 레이어 발행 (Shp 데이터)
    @PostMapping("/publish-layer")
    public ResponseEntity<?> publishLayer(@RequestParam Long resultNo) throws IOException {
        gisService.publishLayer(resultNo);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 공통레이어 가져오기
    @GetMapping("/common-layer")
    public ResponseEntity<?> getProtectedArea(@RequestParam(name = "sgg", defaultValue = "") String sgg) throws IOException {
        ArrayList<GisDto.CommonLayer> commonLayer = gisService.getCommonLayer(sgg);


        return new ResponseEntity<>(commonLayer,HttpStatus.OK);
    }

    // 주차장 현황 가져오기
    @GetMapping("/current-layer")
    public ResponseEntity<?> getCurrentPkLayer() throws IOException {
        String currentPkLayer = gisService.getCurrentPkLayer();

        return new ResponseEntity<>(currentPkLayer,HttpStatus.OK);
    }
}