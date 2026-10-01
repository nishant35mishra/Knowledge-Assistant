package com.project.knowledgeassistant.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String test(){
        return "home Page ";
    }

    @GetMapping("/test1")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String TestController1(){

        return "success" ;

    }

    @GetMapping("/test2")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String TestController2(){

        return "success" ;

    }

    @GetMapping("/test3")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String TestController3(){

        return "success" ;

    }

    @GetMapping("/test4")
    @PreAuthorize("hasRole('PUBLIC')")
    public String TestController4(){

        return "success" ;

    }

    @GetMapping("/test5")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public String TestController5(){

        return "success" ;

    }
}
