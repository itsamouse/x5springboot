package net.jingbo.x5springboot.api.controller;

import net.jingbo.x5springboot.api.service.BaasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018-4-26.
 */
@Controller
@RequestMapping("/yfecp/yfecp_service")
public class YFBaasController {

    private final Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    BaasService baasService;

    @PostMapping(value={"/query_std_*","/save_std_*"})
    public void service(ServletRequest request, ServletResponse response) throws ServletException {
        HttpServletRequest reg = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        baasService.execService(reg, resp);
    }



}
