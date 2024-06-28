package com.example.demo;

import com.example.demo.domain.system.user.access.RoleRepository;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.service.system.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    RoleRepository roleRepo;

    @Test
    void contextLoads() {
    }


}
