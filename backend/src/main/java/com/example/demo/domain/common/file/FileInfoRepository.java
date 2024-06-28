package com.example.demo.domain.common.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    /*
    camelCase 가 아니면 jpa 에서 인식하지 못할 가능성이 있습니다. snake 사용 지양.
     */
//    Optional<FileInfo> findByFile_nm(String fileNm);
    Page<FileInfo> findAll(Pageable pageable);

    List<FileInfo> findByFilePath(String path);

    void deleteAllByFilePath(String filePath);

}
