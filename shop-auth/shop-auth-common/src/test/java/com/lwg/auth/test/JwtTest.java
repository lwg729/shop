package com.lwg.auth.test;

import com.lwg.entity.UserInfo;
import com.lwg.utils.JwtUtils;
import com.lwg.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/2 20:12
 */
public class JwtTest {

    private static final String pubKeyPath = "F:\\rsa\\rsa.pub";

    private static final String priKeyPath = "F:\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;


    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "109837");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "lwg"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoibHdnIiwiZXhwIjoxNjE0NjkxMDY0fQ.jR-J5UfaEqnNU9OHLYq2voBlpF4neJJJZJZoUsd3kF18FTtSifQk7b9k-7WyEyMp8z9u3B__lAyXIQPQk5rmurfsxJugHoD4BCg9sCLB3NbluKW8-aRNQtQ3-UIvmi2v6aza8j_gKQt95if5pC1eqDY764Qpjjiz-IgPDYCkqUc";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
