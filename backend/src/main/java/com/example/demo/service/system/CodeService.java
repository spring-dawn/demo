package com.example.demo.service.system;

import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.dto.system.CodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CodeService {
    private final CodeRepository codeRepo;

//    public CodeDto.CodeRes selectCode(Long id) {
//        Optional<Code> codeOptional = codeRepo.findById(id);
//        if (!codeOptional.isPresent()) {
//            throw new EntityNotFoundException("Can't find code.");
//        }
//        Code code = codeOptional.get();
//
////        code.setAuthorities(
////                Stream.concat(
////                getRoles(code.getRoles()).stream(),
////                getPrivileges(code.getRoles()).stream()
////        ).collect(Collectors.toList()));
//
//        return code.toCodeRes();
//    }

    public CodeDto selectCode(Long id) {
        Code code = codeRepo.findById(id).orElseThrow(EntityNotFoundException::new);

        return code.toCodeRes();
    }

    public CodeDto selectCodeByName(String name) {
        Code code = codeRepo.findByName(name).orElseThrow(EntityNotFoundException::new);

        return code.toCodeRes();
    }

    public List<CodeDto> selectList() {
        return codeRepo.findByParentIsNull().stream()
                .map(Code::toCodeRes)
                .collect(Collectors.toList());
    }


    /**
     * 새 코드 등록
     * @param req 코드명(uk), 계층(depth), 상위 코드 id.
     * @return 연관 필드를 제외한 결과
     */
    @Transactional
    public CodeDto createCode(CodeDto.CodeReq req) {
//        1) uk name 중복 확인
        Optional<Code> optional = codeRepo.findByName(req.getName());
        if(optional.isPresent()) throw new EntityExistsException("This code name already exists.");

//        2) 생성
        Code parent = req.getParentId() == null ? null : codeRepo.findById(req.getParentId()).orElse(null);
        Code code = Code.builder()
                .name(req.getName())
                .value(req.getValue())
//                .depth(req.getDepth())
                .comment(req.getComment())
                .parent(parent)
                .build();
        codeRepo.save(code);

//        3) res
        return code.toCodeRes();
    }


    /**
     * 코드 수정
     * @param req name, value, comment, Long parentId
     * @return 수정 결과
     */
    @Transactional
    public CodeDto updateCode(CodeDto.CodeReq req){
//        1) find target
        Code code = codeRepo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find code."));

//        2) uk validation
        if(!StringUtils.hasText(req.getName())) throw new NullPointerException("Required value is empty.");

        List<Code> list = codeRepo.findAll();
        for( Code c : list){
            if(req.getName().equals(c.getName()) && req.getId() != c.getId()){
                throw new EntityExistsException("This code name already exists.");
            }
        }

//        3) update
        code.update(req);
        if(req.getParentId() != null){
            if(req.getParentId() != code.getId()){
                code.updateParent(codeRepo.findById(req.getParentId()).orElse(null));
            }else {
                throw new RuntimeException("This cannot be parent code.");
            }
        }

//        4) res
        return code.toCodeRes();
    }


    @Transactional
    public CodeDto deleteCode(Long id) {
//        1) find target
        Code code = codeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find code."));

//        2) delete and res
        codeRepo.delete(code);
        return code.toCodeRes();
    }


}