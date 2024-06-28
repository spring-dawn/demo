package com.example.demo.controller.data.facility;

import com.example.demo.domain.api.building_management.BuildingManagementRepoCustom;
import com.example.demo.dto.api.BuildingManagementDto;
import com.example.demo.service.api.bm.BuildingManagementApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/facility/read/sub",
        produces = "application/json"
)
public class PFSubController {
    /*
    건축물관리대장 buildingManagement 를 주차시설-부설 주차장 데이터로 취급합니다
     */
    private final BuildingManagementApiService service;

    private final BuildingManagementRepoCustom query;

    @GetMapping("/search")
    public ResponseEntity<?> search(BuildingManagementDto.BuildingManagementReq req) {
        return new ResponseEntity<>(query.search(req), HttpStatus.OK);
    }

    @GetMapping("/excel")
    public void excelDownload(HttpServletResponse response, BuildingManagementDto.BuildingManagementReq req){
        service.excelDownload(response, req);
    }


}
