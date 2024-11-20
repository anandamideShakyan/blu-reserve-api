package com.example.blureserve.apis;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtGenerator {
    private static final String SECRET_KEY = "b9546df08f44127d3415ec7a491c19be95f204302a82c07323a89db0764f0692";

    public static void main(String[] args) {
        String jwt = Jwts.builder()
        .setSubject("12345") // Optional user identifier
        .claim("location", "pyramid")
        .claim("slot", "19:00-19:30")
        .claim("date", "2024-11-19")
        .claim("seats", 5)
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // Sign with your secret key
        .compact();

System.out.println("Generated JWT: " + jwt);

    }
}
