package com.cmpe275.openhome.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = {"/", ""})
    public String getIndex(HttpServletRequest request) {
        return "index.html";
    }

}