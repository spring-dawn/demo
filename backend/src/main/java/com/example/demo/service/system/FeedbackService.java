package com.example.demo.service.system;

import com.example.demo.atech.Msg;
import com.example.demo.domain.system.feedback.Feedback;
import com.example.demo.domain.system.feedback.FeedbackRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.system.FeedbackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static com.example.demo.atech.MyUtil.getEnum;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {
    private final String THIS = "요청";
    private final FeedbackRepository repo;
    private final UserRepository userRepo;

    // selectList 생략

    public FeedbackDto selectOne(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }

    // insert
    @Transactional
    public FeedbackDto insertOne(FeedbackDto.Req req) {
        // 제목, 내용 비워놓을 거면 건의 게시판을 왜 씁니까
        if (!hasText(req.getTitle()) || !hasText(req.getContents())) throw new NullPointerException(Msg.NPE.getMsg());

        // 세션에서 사용자 정보
        Authentication session = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUserId(session.getPrincipal().toString())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "사용자")));

        return repo.save(
                Feedback.builder()
                        .sggCd(user.getAgency())
                        .dept(user.getDept())
                        .title(req.getTitle())
                        .contents(req.getContents())
                        // hit 기본 0
                        .status("0")
                        .build()
        ).toRes();
    }

    // update
    @Transactional
    public FeedbackDto updateOne(FeedbackDto.Req req) {
        Feedback fb = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        fb.update(req);
        return fb.toRes();
    }

    // update hit, status
    @Transactional
    public void updateHit(Long id) {
        Feedback target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.updateHit();
    }

    @Transactional
    public void updateStatus(Long id) {
        Feedback target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.updateStatus("1");
    }

    // delete
    @Transactional
    public FeedbackDto deleteOne(Long id) {
        Feedback target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        repo.delete(target);
        return target.toRes();
    }

}
