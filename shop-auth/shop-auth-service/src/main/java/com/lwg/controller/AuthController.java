package com.lwg.controller;

import com.lwg.common.utils.CookieUtils;
import com.lwg.config.JwtProperties;
import com.lwg.entity.UserInfo;
import com.lwg.service.impl.AuthServiceImpl;
import com.lwg.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/2 21:42
 */

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               HttpServletRequest request, HttpServletResponse response) {

        //登录校验
        String token = authService.accredit(username, password);

        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //将token写入cookie 并指定httpOnly为true,防止通过JS获取和修改
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);

        return ResponseEntity.ok(null);
    }

    /**
     * 校验用户信息
     *
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LW_TOKEN") String token,HttpServletRequest request,
                                               HttpServletResponse response) {
        try {
            //从token中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //解析成功要重新刷新token
            JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            //更新cookie中的token
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
            //解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出现异常则，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
