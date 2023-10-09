package com.example.demo.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.config.UserPricipal;
import com.example.demo.constants.SecurityConstant;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserPricipal userPricipal){
        String[] claims = getClaimsFromUser(userPricipal);

        return JWT.create()
                .withIssuer(SecurityConstant.GET_ARRAYS)
                .withAudience(SecurityConstant.ADMINISTRATION)
                .withIssuedAt(new Date()).withSubject(userPricipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);

        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token){
        JWTVerifier verifier = getJWTVerifier();

        return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    public Authentication getAuthentication(String username,
                                            List<GrantedAuthority> authorities,
                                            HttpServletRequest request){
        UsernamePasswordAuthenticationToken userPasswordToken = new UsernamePasswordAuthenticationToken(username,null,authorities);

        userPasswordToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return userPasswordToken;
    }

    public boolean isTokenValid(String username,String token){
        JWTVerifier verifier = getJWTVerifier();

        return !StringUtils.isEmpty(username) && !isTokenExpired(verifier,token);
    }

    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    public JWTVerifier getJWTVerifier(){
        JWTVerifier jwtVerifier;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            jwtVerifier = JWT.require(algorithm).withIssuer(SecurityConstant.GET_ARRAYS).build();
        }catch (JWTVerificationException ex){
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

    private boolean isTokenExpired(JWTVerifier verifier,String token){
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromUser(UserPricipal userPricipal){
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userPricipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}



















