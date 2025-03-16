package com.learn.user.controller;

import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.learn.common.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前后端分离架构下集成SSO所需的接口
 */
@RestController
public class H5Controller {

    // 当前是否登录 
    @GetMapping("/sso/isLogin")
    public Response<Boolean> isLogin() {
        return Response.ok(StpUtil.isLogin());
    }
    
    // 返回SSO认证中心登录地址 
    @GetMapping("/sso/getSsoAuthUrl")
    public Response<String> getSsoAuthUrl(String clientLoginUrl) {
        String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
        return Response.ok(serverAuthUrl);
    }
    
    // 根据ticket进行登录
    @PostMapping("/sso/doLoginByTicket")
    public Response<String> doLoginByTicket(String ticket) {
        SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket, "/sso/doLoginByTicket");
        StpUtil.login(ctr.loginId, ctr.remainSessionTimeout);
        return Response.ok(StpUtil.getTokenValue());
    }
    
}