package com.rainsoul.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rainsoul.bilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "rainsoul";

    /**
     * 生成JWT Token。
     *
     * @param userId 用户ID，用于生成Token的唯一标识。
     * @return 返回生成的JWT Token字符串。
     * @throws Exception 如果JWT生成过程中遇到异常，则抛出。
     */
    public static String generateToken(Long userId) throws Exception {
        // 使用RSA算法配置JWT
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        // 获取当前时间并设置Token过期时间为当前时间后30秒
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 300000);
        // 创建并签署JWT，包含用户ID、发行者、过期时间等信息
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    /**
     * 验证JWT令牌的有效性。
     *
     * @param token 待验证的JWT令牌字符串。
     * @return 验证成功返回JWT令牌中的用户ID，通常是一个Long值。
     * @throws ConditionException 如果令牌过期或无效，抛出此异常。
     */
    public static Long verifyToken(String token) {
        try {
            // 使用RSA算法配置JWT，基于公钥和私钥进行加解密
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            // 创建JWT验证器，准备验证token
            JWTVerifier verifier = JWT.require(algorithm).build();
            // 执行验证，并返回解码后的JWT对象
            DecodedJWT jwt = verifier.verify(token);
            // 从JWT对象中提取并返回用户ID
            return Long.valueOf(jwt.getKeyId());
        } catch (TokenExpiredException e) {
            // 处理令牌过期的情况
            throw new ConditionException("555", "token过期");
        } catch (Exception e) {
            // 处理其他异常情况，例如令牌格式错误等
            throw new ConditionException("token无效");
        }

    }
}
