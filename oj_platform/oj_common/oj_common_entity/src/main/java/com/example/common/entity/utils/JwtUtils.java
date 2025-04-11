package com.example.common.entity.utils;

import com.example.common.entity.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUtils {
    /**
     * 生成令牌
     *
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, String secret) {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据
     *
     * @param token  令牌
     * @param secret 密钥
     * @return 数据
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public static String getAdminKey(Claims claims) {
        Object value = claims.get(JwtConstants.ADMIN_KEY);
        return toStr(value);
    }

    public static String getAdminId(Claims claims) {
        Object value = claims.get(JwtConstants.ADMIN_ID);
        return toStr(value);
    }

    private static String toStr(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static void main(String[] args) {
//        Map<String, Object> claims = new HashMap<String, Object>();
//        claims.put("adminId", 666666L);
//        String token = createToken(claims, "123456789123456789123456789123456789");
//        System.out.println(token);
        Claims claims1 = parseToken("eyJhbGciOiJIUzUxMiJ9.eyJhZG1pbklkIjo2NjY2NjZ9.BlD2eN7HwqVVGB6gzwYnsktOr-7jKmA-lXQ5FAnJRF0uH8j5lg8YRGkR_7tax9nV-2YRCFz7uv4iqYIeU4l0uw",
                "123456789123456789123456789123456789");
        System.out.println(claims1);
    }
}