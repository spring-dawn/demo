package com.example.demo.service.common;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.dto.common.FileInfoDto;
import com.example.demo.service.GisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {
    private final FileInfoRepository fileInfoRepo;

    /* 일반, shapefile 저장소 분리 */
    @Value("${spring.servlet.multipart.location}")
    private String fileDir;

    @Value("${spring.servlet.multipart2.shp}")
    private String fileDirSHP;

    /* 임시파일 저장소 */
     @Value("${spring.servlet.multipart2.tmp}")
    private String tmpDir;

    /*
    서비스 로직
     */

    // 전체 파일 리스트 조회
    public List<FileInfoDto.FileInfoRes> selectList() {
        List<FileInfo> list = fileInfoRepo.findAll();

        return list.stream()
                .map(FileInfo::toFileInfoRes)
                .collect(Collectors.toList());
    }


    // 상세 조회
    public FileInfoDto.FileInfoRes selectFile(Long id) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepo.findById(id);
        if (!optionalFileInfo.isPresent()) {
            throw new EntityNotFoundException("No such file.");
        }

        return optionalFileInfo.get().toFileInfoRes();
    }


    // (임시)파일 업로드
    @Transactional
    public List<FileInfoDto.FileInfoRes> saveFile(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) throw new FileNotFoundException("업로드 할 파일이 발견되지 않았습니다.");

        // 리턴 리스트
        List<FileInfo> result = new ArrayList<>();

        for (MultipartFile file : files) {
            // 파일 이름 추출
            String fileName = file.getOriginalFilename();
            // 파일 이름으로 쓸 uuid 생성
            String uuid = UUID.randomUUID().toString();
            // 확장자 추출
            String extension = MyUtil.getFileNmOrExt(fileName, false);
            String savedName = uuid + extension;
            // 파일 저장 경로
//            String savedPath = fileDir;
            String savedPath = tmpDir;

            // 파일 데이터(엔티티) 생성, db 에 저장
            FileInfo fileInfo = FileInfo.builder()
                    .fileNm(fileName)
                    .fileNmStored(savedName)
                    .filePath(savedPath)
                    .build();

//            DB에 파일 데이터 저장
            result.add(fileInfoRepo.save(fileInfo));

//            실제 파일 저장: shp 파일이면 원파일명 그대로 저장합니다
            if(Arrays.asList(GisService.SHP_FILE).contains(extension)){
                file.transferTo(new File(savedPath + fileName));
            }else {
                file.transferTo(new File(savedPath + savedName));
            }
        }

        return result.stream()
                .map(FileInfo::toFileInfoRes)
                .collect(Collectors.toList());
    }

//    /**
//     * 파일 업로드, 업로드 기록 저장
//     * @param files 파일
//     * @return res
//     */
//    @Transactional
//    public List<FileInfoDto.FileInfoRes> uploadFiles(List<MultipartFile> files) {
////        1)
//        if (files == null || files.isEmpty()) throw new NullPointerException(Msg.NPE.getMsg());
////        2)
//        List<FileInfo> result = new ArrayList<>();
//
//        try {
//            for (MultipartFile file : files) {
//                // 파일명, uuid, 확장자
//                String fileNm = file.getOriginalFilename();
//                String uuid = UUID.randomUUID().toString();
//                String ext = MyUtil.getFileNmOrExt(fileNm, false);
//                String uuNm = uuid + ext;
//                // 저장
//                FileInfo fileInfo = FileInfo.builder()
//                        .fileNm(fileNm)
//                        .fileNmStored(uuNm)
//                        .filePath(tmpDir)
//                        .build();
//                result.add(fileInfoRepo.save(fileInfo));
//                // 실제 파일 경로 지정. shp 파일이면 원본명 유지(이후 postgres 테이블화에 필요)
//                String savedNm = Arrays.asList(GisService.SHP_FILE).contains(ext) ? tmpDir + fileNm : tmpDir + uuNm;
//                file.transferTo(new File(savedNm));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        3)
//        return result.stream()
//                .map(FileInfo::toFileInfoRes)
//                .collect(Collectors.toList());
//    }


    // 파일 다운로드
    public Map<String, Object> downloadFile(Long id) {
        Map<String, Object> fileData = new HashMap<>();

//        1) 타깃 확인
        FileInfo fileInfo = fileInfoRepo.findById(id).orElseThrow(EntityNotFoundException::new);

//        2) 파일 생성
        Resource file = null;
        String encNm = null;
        try {
            boolean isShp = Arrays.asList(GisService.SHP_FILE).contains(MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false));
            if (isShp) {
                file = new FileSystemResource(fileInfo.getFilePath() + fileInfo.getFileNm());
            } else {
                file = new FileSystemResource(fileInfo.getFilePath() + fileInfo.getFileNmStored());
            }

            if (!file.exists()) throw new FileNotFoundException(Msg.NO_FILES.getMsg());
            encNm = URLEncoder.encode(fileInfo.getFileNm(), "UTF-8").replaceAll("\\+", "%20");
        } catch (IOException e) {
            log.error("Error={}", e.getMessage());
            throw new RuntimeException("파일 다운로드 중 오류가 있습니다.");
        }

//        3) 리턴
        fileData.put("file", file);
        fileData.put("contentDisposition", "attachment; filename=" + encNm);

        return fileData;
    }


    /**
     * 임시 파일을 본 저장소로 이동
     * @param id 파일 식별자
     * @return 이동 후 파일 상태
     */
    @Transactional
    public FileInfoDto.FileInfoRes moveFile(Long id) {
//        1) 타깃 데이터 확인
        FileInfo fileInfo = fileInfoRepo.findById(id).orElseThrow(EntityNotFoundException::new);

//        2) 실제 파일 확인: shp 여부 검사
        boolean isShp = Arrays.asList(GisService.SHP_FILE).contains(MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false));
        String path = "";
        File currentFile;

        if (isShp) {
            path = fileDirSHP;
            currentFile = new File(fileInfo.getFilePath() + fileInfo.getFileNm());
        } else {
            path = fileDir;
            currentFile = new File(fileInfo.getFilePath() + fileInfo.getFileNmStored());
        }

//        3) 저장 경로 확인(없으면 생성), 실제 파일 이동
        try {
            MyUtil.mkDirAuto(path);
            currentFile.renameTo(new File(path + currentFile.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        4) db 파일 데이터 변경
        fileInfo.updateFilePath(path);

//        5) res
        return fileInfo.toFileInfoRes();
    }

    /**
     * 임시 파일을 본 저장소로 이동
     * @param id 파일 식별자
     * @return 이동 후 파일 상태
     */
    @Transactional
    public FileInfo moveFile(Long id, String tablePath) {
//        1) 타깃 데이터 확인
        FileInfo fileInfo = fileInfoRepo.findById(id).orElseThrow(EntityNotFoundException::new);

//        2) 실제 파일 확인: shp 여부 검사
        boolean isShp = Arrays.asList(GisService.SHP_FILE).contains(MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false));
        String path = "";
        File currentFile;

        if (isShp) {
            path = fileDirSHP + tablePath + "/";
            currentFile = new File(fileInfo.getFilePath() + fileInfo.getFileNm());
        } else {
            path = fileDir + tablePath + "/";
            currentFile = new File(fileInfo.getFilePath() + fileInfo.getFileNmStored());
        }

//      3) 저장 경로 확인(없으면 생성), 실제 파일 이동
        try {
            MyUtil.mkDirAuto(path);
            currentFile.renameTo(new File(path + currentFile.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        4) db 파일 데이터 변경
        fileInfo.updateFilePath(path);

//        5) res
        return fileInfo;
    }

    // 파일 경로 자동생성

    // 파일 삭제
    @Transactional
    public FileInfoDto.FileInfoRes deleteFile(Long id) {
//        1) 타깃 확인
        FileInfo fileInfo = fileInfoRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(Msg.NO_FILES.getMsg()));

//        2) 실제 파일 경로 확인
        File file;
        // shp?
        boolean isShp = Arrays.asList(GisService.SHP_FILE).contains(MyUtil.getFileNmOrExt(fileInfo.getFileNm(), false));
        if(isShp){
            file = new File(fileInfo.getFilePath() + fileInfo.getFileNm());
        }else{
            file = new File(fileInfo.getFilePath() + fileInfo.getFileNmStored());
        }

//         3) 실제 파일, db 데이터 삭제
        boolean isDeleted = file.delete();
        if(!isDeleted) throw new RuntimeException("파일이 삭제되지 않았습니다. 문제를 확인해주세요.");
        fileInfoRepo.delete(fileInfo);

//        4) res
        return fileInfo.toFileInfoRes();
    }


    // 임시파일 삭제
    @Transactional
    public String deleteFileTmp() {
        return null;
    }

    // 파일 확장자 구하기
    public String getExtenstion(MultipartFile files) throws IOException {
        if (files.isEmpty()) {
            return null;
        }
        String fileName = files.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));

        return extension;
    }

    // 파일 읽기
    public List<String[]> readDataFile(MultipartFile files) throws IOException {
//        String extension = getExtenstion(files).toUpperCase();
        String extension = MyUtil.getFileNmOrExt(files.getOriginalFilename(), false).toUpperCase();

        List<String[]> list = new ArrayList<>();

        if (extension.equals(".XLS") || extension.equals(".XLSX")) {
            /*xls, xlsx*/
            Workbook workbook;
            if (extension.equals(".XLS")) {
                workbook = new HSSFWorkbook(files.getInputStream());
            } else {
                workbook = new XSSFWorkbook(files.getInputStream());
            }

            workbook.getNumberOfSheets();
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("Sheet Name : " + sheet.getSheetName() + "\n");

                Iterator<Row> rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {
                    List<String> line = new ArrayList();
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        switch (cell.getCellType()) {
                            case BOOLEAN:
                                line.add(Boolean.toString(cell.getBooleanCellValue()));
                                break;
                            case NUMERIC:
                                line.add(Double.toString(cell.getNumericCellValue()));
                                break;
                            case STRING:
                                line.add(cell.getStringCellValue());
                                break;
                            case FORMULA:
                                line.add(cell.getCellFormula());
                                break;
                        }// switch
                    }// while
                    list.add(line.toArray(new String[line.size()]));
                }// while
            }// for
        } else if (extension.equals(".TXT") || extension.equals(".CSV")) {
            /* csv, txt */
            BufferedReader br = new BufferedReader(new InputStreamReader(files.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                list.add(row);
            }
            br.close();
        } else {
            // 위 확장자가 아닐경우 리턴
            System.out.println("Error 형식 안맞는 파일");
            return null;
        }
        return list;
    }

    public void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // 재귀적으로 하위 디렉토리 삭제
                    } else {
                        file.delete(); // 파일 삭제
                    }
                }
            }

            directory.delete(); // 빈 디렉토리 삭제
        }
    }




}
