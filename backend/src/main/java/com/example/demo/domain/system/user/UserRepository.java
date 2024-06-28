package com.example.demo.domain.system.user;

import com.example.demo.domain.system.user.access.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // 중복 검사
    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);

    long countByRole(Role role);
    List<User> findAllByRole(Role role);

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);

    Page<User> findAll(Pageable pageable);

    // TODO: IgnoreCase 필요없을듯. insert 될 때 소문자만 들어가게 할 것.
    Page<User> findByUserIdContainsIgnoreCase(String userId, Pageable pageable);
}