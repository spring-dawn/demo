package com.example.demo.domain.system.user;

import com.example.demo.dto.system.UserDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustom {
    /*
    querydsl 쿼리
     */
    private final JPAQueryFactory factory;
    QUser user = QUser.user;


    /**
     * 사용자 정보 검색
     *
     * @param req 검색조건
     * @return 검색결과
     */
    public List<UserDto.UserRes> searchUsers(UserDto.UserReq req) {
        return factory
                .selectFrom(user)
                .where(
                        // and 조건 적용
                        containUserId(req.getUserId())
                        , containUserNm(req.getUserNm())
                        , containEmail(req.getEmail())
                        , eqUseYn(req.getUseYn())
                        , eqAgency(req.getAgency())
                )
                .fetch()
                .stream().map(User::toRes)
                .sorted(Comparator.comparing(UserDto.UserRes::getCreateDtm))
                .collect(Collectors.toList());
    }


    /*
    exprressions 분리
     */
    private BooleanExpression containUserId(String userId) {
        return hasText(userId) ? user.userId.contains(userId) : null;
    }

    private BooleanExpression containUserNm(String userNm) {
        return hasText(userNm) ? user.userNm.contains(userNm) : null;
    }

    private BooleanExpression containEmail(String email) {
        return hasText(email) ? user.email.contains(email) : null;
    }

//    private BooleanExpression eqAdmYn(String admYn) {
//        return hasText(admYn) ? user.admYn.eq(admYn) : null;
//    }

    private BooleanExpression eqUseYn(String useYn) {
        return hasText(useYn) ? user.useYn.eq(useYn) : null;
    }

    private BooleanExpression eqAgency(String agency) {
        return hasText(agency) ? user.agency.eq(agency) : null;
    }

}


