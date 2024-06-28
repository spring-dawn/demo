package com.example.demo.controller.system;

import com.example.demo.dto.system.MenuDto;
import com.example.demo.service.system.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(
        value = "/api",
        produces = "application/json"
)
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/menus")
    ResponseEntity<?> getList() {
        return new ResponseEntity<>(menuService.getMenuList(), HttpStatus.OK);
    }

    @GetMapping("/mymenu")
    ResponseEntity<?> getMyMenuList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(menuService.getMyMenuList(authentication), HttpStatus.OK);
    }

    @GetMapping("/menu/tabs")
    ResponseEntity<?> getTabs(@RequestParam String parentUrl) {
        List<MenuDto> res = menuService.getTabs(parentUrl);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}