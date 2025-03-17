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
    /*
     * SSO-Client端：处理所有SSO相关请求
     *         http://{host}:{port}/sso/login          -- Client端登录地址，接受参数：back=登录后的跳转地址
     *         http://{host}:{port}/sso/logout         -- Client端单点注销地址（isSlo=true时打开），接受参数：back=注销后的跳转地址
     *         http://{host}:{port}/sso/logoutCall     -- Client端单点注销回调地址（isSlo=true时打开），此接口为框架回调，开发者无需关心
     */
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoClientProcessor.instance.dister();
    }
    
}