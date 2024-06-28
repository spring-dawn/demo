package com.example.demo.domain.data.research.shp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShpResultRepository extends JpaRepository<ShpResult, Long> {
    ShpResult findByTableName(String tableName);
    ShpResult findBySubTypeAndCardYnAndYearAndRegCode(String subType, String cardYn, String year, String reg);

    @Query(value = "SELECT JSONB_BUILD_OBJECT(" +
            "'fid', fid, " +
            "'properties', TO_JSONB(inputs) - 'the_geom', " +
            "'geometry', ST_AsGeoJSON(ST_Transform(the_geom, 3857))::JSONB) " +
            "FROM (SELECT * FROM public.:tableName) inputs",
            nativeQuery = true)
    List<Object> customJsonQuery(@Param("tableName") String tableName);
}