package com.eventory.server.global.security.jwt;

import com.eventory.server.domain.member.entity.LoginInfo;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.LoginInfoRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.handler.MemberHandler;
import com.eventory.server.global.security.userDetails.LocalCustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final LoginInfoRepository loginInfoRepository;

    private static final String HEADER_STRING = "Authorization";
    private static final String HEADER_STRING_PREFIX = "Bearer ";

    @Value("${jwt.token.secretKey}")
    private String signingKey;

    @Value("${jwt.token.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.token.expiration.refresh}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication, long validityMilliseconds) {
        String username = authentication.getName();

        LoginInfo loginUserLoginInfo = loginInfoRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.LOGIN_INFO_NOT_FOUND));

        Member loginUser = loginUserLoginInfo.getMember();

        Long UserId = loginUser.getId();

        return Jwts.builder()
                .setSubject(username)
                .claim("id",UserId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMilliseconds))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        return generateToken(authentication, accessTokenExpiration);
    }

    public String createRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshTokenExpiration);
    }

    // 토큰 추출
    public String extractToken (final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HEADER_STRING);

        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_STRING_PREFIX)) {
            return authorizationHeader.substring(7);
        }
        throw new MemberHandler(ErrorStatus.INVALID_TOKEN);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();

        LoginInfo loginInfo = loginInfoRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND_LOGIN));

        Member member = loginInfo.getMember();
        LocalCustomUserDetails principal = new LocalCustomUserDetails(member, loginInfo);

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                principal.getAuthorities()
        );
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer " .length());
        }
        return null;
    }

    public Authentication extractAuthentication(HttpServletRequest request){
        String accessToken = resolveToken(request);
        if(accessToken == null || !validateToken(accessToken)) {
            throw new MemberHandler(ErrorStatus.INVALID_TOKEN);
        }
        return getAuthentication(accessToken);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("id", Long.class);
    }
}