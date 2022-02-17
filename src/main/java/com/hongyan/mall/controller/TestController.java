package com.hongyan.mall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sixiaojie
 * @date 2022-02-17-10:28
 */
@RestController
@RequestMapping("/mall/api/test")
public class TestController {

    @GetMapping("/testHttps")
    public String testHttps(){
        return "success";
    }
}
