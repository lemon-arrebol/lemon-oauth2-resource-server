package com.lemon.oauth2.controller;

import com.lemon.oauth2.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author lemon
 * @description
 * @date 2020-05-07 19:38
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/auth/")
public class AuthController {
    /**
     * @return com.lemon.oauth2.vo.ResponseVO
     * @description
     * @author lemon
     * @date 2020-05-07 19:38
     */
    @GetMapping("user/login")
    public ResponseVO login() {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setData(UUID.randomUUID().toString());
        return responseVO;
    }
}
