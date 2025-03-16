package com.learn.auth.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.stp.SaTokenInfo;
import com.learn.api.domain.dto.auth.UserLoginDto;
import com.learn.auth.service.LoginService;
import com.learn.common.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Server端 Controller 
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SsoServerController {
    private final LoginService loginService;

    /**
     * SSO-Server端：处理所有SSO相关请求 (下面的章节我们会详细列出开放的接口) 
     */
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoServerProcessor.instance.dister();
    }
    
    /**
     * 配置SSO相关参数 
     */
    @Autowired
    private void configSso(SaSsoServerConfig ssoServer) {
        // 配置：未登录时返回的View 
        ssoServer.notLoginView = () -> {
            String msg = "当前会话在SSO-Server端尚未登录，请先访问"
                    + "<a href='/sso/doLogin?name=sa&pwd=123456' target='_blank'> doLogin登录 </a>"
                    + "进行登录之后，刷新页面开始授权";
            return msg;
        };
        
        // 配置：登录处理函数 
        ssoServer.doLoginHandle = (name, pwd) -> {
            Integer code = null;
            if(pwd==null){
                code=Integer.valueOf(SaHolder.getRequest().getParam("code"));
            }
            String phone = SaHolder.getRequest().getParam("phone");
            SaTokenInfo tokenInfo = loginService.login(new UserLoginDto(pwd, code, phone));
            log.info("登录成功");
            return Response.ok(tokenInfo);


        };
        
        // 配置 Http 请求处理器 （在模式三的单点注销功能下用到，如不需要可以注释掉） 
        /*ssoServer.sendHttp = url -> {
            try {
                System.out.println("------ 发起请求：" + url);
                String resStr = Forest.get(url).executeAsString();
                System.out.println("------ 请求结果：" + resStr);
                return resStr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };*/
    }
    
}
