package com.example.blureserve.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blureserve.models.Book;
import com.example.blureserve.services.ReservationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.interfaces.RSAPublicKey;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    private static String getUserID(String jwt) throws Exception {
        String jwkJson = "{\n" +
                "  \"kty\": \"RSA\",\n" +
                "  \"x5t#S256\": \"FUod_Opcs2jU0_C651e1RzWdtbm3zPSOObjgNSIa3eg\",\n" +
                "  \"e\": \"AQAB\",\n" +
                "  \"use\": \"sig\",\n" +
                "  \"kid\": \"oidc-signing-cert-2023\",\n" +
                "  \"x5c\": [\n" +
                "    \"MIIFZjCCBE6gAwIBAgIQC2dEGOgRAn/2su0urBlhXTANBgkqhkiG9w0BAQsFADBqMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSkwJwYDVQQDEyBEaWdpQ2VydCBBc3N1cmVkIElEIENsaWVudCBDQSBHMjAeFw0yMzA4MjkwMDAwMDBaFw0yNjA4MjkyMzU5NTlaMIGHMQswCQYDVQQGEwJVUzERMA8GA1UECBMITmV3IFlvcmsxDzANBgNVBAcTBkFybW9uazE0MDIGA1UEChMrSW50ZXJuYXRpb25hbCBCdXNpbmVzcyBNYWNoaW5lcyBDb3Jwb3JhdGlvbjEeMBwGA1UEAxMVdGVzdC5sb2dpbi53My5pYm0uY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2jB0kRGF/iBO1yiX77qwT049zyOAnZRLlavn72sZvEmR/d7U6/Z/CtztHHumtCZZT14jFWCPKnzhIQ+DvpgqfchWEkd2BlBTSqGaQ9F4AlhFU4885KTsyeVLZ+P7f6lWiO9br43fg5wBgpEoixSkLJCgdXf/MwQO7PmbWN4WGLfP5ARjVdjSrfSSfegJCFKju7nANw/9NhG80Nr0lCY+6nYXpROZ19Sedk7Vp6Zmu+Va7i5qQ9Jhl7+DIhz3KTcJCc1DJ0zgVnl9L8COCfIHqXgWvdCynhojLDbST0Ke860Q3DxD3rQcmtK/LKRUO3FmunnMMXJajRl7f59njgL3hQIDAQABo4IB6DCCAeQwHwYDVR0jBBgwFoAUpWIgUNy7W1eXrSOPNeJUbKl++U4wHQYDVR0OBBYEFLb/eNw2ERX85WykVV7ozxEM1AE6MCEGA1UdEQQaMBiBFmVkLmtsZW5vdGl6QHVzLmlibS5jb20wDgYDVR0PAQH/BAQDAgbAMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDBDBgNVHSAEPDA6MDgGCmCGSAGG/WwEAQIwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzCBiwYDVR0fBIGDMIGAMD6gPKA6hjhodHRwOi8vY3JsMy5kaWdpY2VydC5jb20vRGlnaUNlcnRBc3N1cmVkSURDbGllbnRDQUcyLmNybDA+oDygOoY4aHR0cDovL2NybDQuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0QXNzdXJlZElEQ2xpZW50Q0FHMi5jcmwwfQYIKwYBBQUHAQEEcTBvMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5kaWdpY2VydC5jb20wRwYIKwYBBQUHMAKGO2h0dHA6Ly9jYWNlcnRzLmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydEFzc3VyZWRJRENsaWVudENBRzIuY3J0MA0GCSqGSIb3DQEBCwUAA4IBAQAMnmQCFMGzmT3DzhySLtDyIyet2CpzGJg54dPbtvtSfr2730iTYF2ZJRwdqpbGbEtqOzBjLzI8GJQV4sSkRuM2zz2mqVLw3VakOhPXWlKkDukqAGOG8yfkyNTnqlnDvABajSbBhpb7awDJmGg7EJMbTIf68ik5AKVYnbu7p2Otfnb9hU+16+bpwvH6lIc+ByfQCPToqXXmIpx/IwXgJ4Vxjh3Rf3NHi6wMDQf+ojIz/X6pKVICdllf3wMKirsmY8cF7PvoIi8KfQPZ2xL4/1Ox1qFe0x8jwZ53rd1Pc4r6Cp00ACPro27gXJPTmMK9ZyTa8vy9nWgP3t0Eym5DVVK+\"\n"
                +
                "  ],\n" +
                "  \"n\": \"2jB0kRGF_iBO1yiX77qwT049zyOAnZRLlavn72sZvEmR_d7U6_Z_CtztHHumtCZZT14jFWCPKnzhIQ-DvpgqfchWEkd2BlBTSqGaQ9F4AlhFU4885KTsyeVLZ-P7f6lWiO9br43fg5wBgpEoixSkLJCgdXf_MwQO7PmbWN4WGLfP5ARjVdjSrfSSfegJCFKju7nANw_9NhG80Nr0lCY-6nYXpROZ19Sedk7Vp6Zmu-Va7i5qQ9Jhl7-DIhz3KTcJCc1DJ0zgVnl9L8COCfIHqXgWvdCynhojLDbST0Ke860Q3DxD3rQcmtK_LKRUO3FmunnMMXJajRl7f59njgL3hQ\"\n"
                +
                "}";

        // Convert the JWK to an RSA public key
        JWK jwk = JWK.parse(jwkJson);
        RSAKey rsaKey = (RSAKey) jwk;
        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

        // Decode and parse the JWT payload
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        // Considering email as UserID for the application.
        return claims.get("emailAddress").toString();
    }

    @GetMapping("/mybookings")
    public ResponseEntity<?> getUserBookings(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader) {

        String userID;

        try {

            // Extract JWT from the Authorization header
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                userID = getUserID(authorizationHeader.substring(7));
            } else {
                return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
        }

        return ResponseEntity.ok(reservationService.getUserBookings(userID));
    }

    @PostMapping("/book")
    public ResponseEntity<Object> bookSeats(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestBody Book bookingRequest) {

        try {

            String userID;
            // Extract JWT from the Authorization header
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                userID = getUserID(authorizationHeader.substring(7));
            } else {
                return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
            }

            // Process the booking request using the service
            return reservationService.bookSeats(
                    bookingRequest.getLocation(),
                    bookingRequest.getSlot(),
                    bookingRequest.getSeats(), userID);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
        }
    }

}
