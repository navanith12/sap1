package com.miraclesoft.datalake.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.SapDataLakeApplication;

@RestController
public class RestartController {
    
//    @Autowired
//    private RestartService restartService;
    
    @PostMapping("/restart")
    public void restart() {
    	SapDataLakeApplication.restart();
    }
    
//    @PostMapping("/restartApp")
//    public void restartUsingActuator() {
//        restartService.restartApp();
//    }
    
}