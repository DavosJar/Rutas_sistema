package com.app_rutas.models;

import com.app_rutas.models.enums.Rol;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JwtUtil {
    private static final String SECRET = "tu_secreto";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    private static final long EXPIRATION_TIME = 86400000;

    private static final ConcurrentHashMap<String, AtomicInteger> tokenVersions = new ConcurrentHashMap<>();

    public static String generateToken(String username, Rol rol) {
        String tokenVersion = getTokenVersion(username);

        return JWT.create()
                .withSubject(username)
                .withClaim("rol", rol.name())
                .withClaim("tokenVersion", tokenVersion)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    private static String getTokenVersion(String username) {
        tokenVersions.putIfAbsent(username, new AtomicInteger(0));
        return String.valueOf(tokenVersions.get(username).get());
    }

    public static DecodedJWT validateToken(String token, Rol... allowedRoles) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM).build();
        DecodedJWT jwt = verifier.verify(token);

        String tokenVersion = jwt.getClaim("tokenVersion").asString();
        String username = jwt.getSubject();

        if (!tokenVersion.equals(getTokenVersion(username))) {
            throw new JWTVerificationException("El token ha sido invalidado.");
        }

        String rolString = jwt.getClaim("rol").asString();
        Rol rolFromToken = Rol.valueOf(rolString);

        for (Rol allowedRole : allowedRoles) {
            if (rolFromToken == allowedRole) {
                return jwt;
            }
        }

        throw new JWTVerificationException("El rol no tiene permisos.");
    }

    public static DecodedJWT validateTokenByUsername(String token, String usernameFromRequest)
            throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM).build();
        DecodedJWT jwt = verifier.verify(token);

        String username = jwt.getSubject();
        String tokenVersion = jwt.getClaim("tokenVersion").asString();

        if (!tokenVersion.equals(getTokenVersion(username))) {
            throw new JWTVerificationException("El token ha sido invalidado.");
        }

        if (!username.equals(usernameFromRequest)) {
            throw new JWTVerificationException("El username no coincide con el token.");
        }

        return jwt;
    }

    public static DecodedJWT validateToken(String token, String usernameFromRequest, Rol... allowedRoles)
            throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM).build();
        DecodedJWT jwt = verifier.verify(token);

        String username = jwt.getSubject();
        String tokenVersion = jwt.getClaim("tokenVersion").asString();
        String rolString = jwt.getClaim("rol").asString();

        if (!tokenVersion.equals(getTokenVersion(username))) {
            throw new JWTVerificationException("El token ha sido invalidado.");
        }

        Rol rolFromToken = Rol.valueOf(rolString);

        for (Rol allowedRole : allowedRoles) {
            if (rolFromToken == allowedRole) {
                return jwt;
            }
        }
        if (username.equals(usernameFromRequest)) {
            return jwt;
        }

        throw new JWTVerificationException("No tiene permisos para acceder a este recurso.");
    }

    public static void invalidateToken(String username) {
        tokenVersions.get(username).incrementAndGet();
    }

    public static Rol getRolFromToken(DecodedJWT jwt) {
        String rolString = jwt.getClaim("rol").asString();
        return Rol.valueOf(rolString);
    }

    public static String extractUsername(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }
}