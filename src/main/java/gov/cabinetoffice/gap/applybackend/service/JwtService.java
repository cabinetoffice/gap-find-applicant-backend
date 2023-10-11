package gov.cabinetoffice.gap.applybackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.config.UserServiceConfig;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

import static java.lang.Boolean.TRUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final UserServiceConfig userServiceConfig;
    private final RestTemplate restTemplate;

    public DecodedJWT decodedJwt(String normalisedJWT) {
        return JWT.decode(normalisedJWT);
    }

    public boolean verifyToken(final String jwt) {
        final String url = userServiceConfig.getDomain() + "/is-user-logged-in";
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", userServiceConfig.getCookieName() + "=" + jwt);
        final HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
        final ResponseEntity<Boolean> isJwtValid = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Boolean.class);
        log.info("is user logged in: " + isJwtValid);

        return TRUE.equals(isJwtValid.getBody());
    }

    public String decodeBase64ToJson(final String base64) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
    }

    public boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(Calendar.getInstance().getTime());
    }

    public JwtPayload decodeTheTokenPayloadInAReadableFormat(DecodedJWT jwt) {

        final String payloadJson = decodeBase64ToJson(jwt.getPayload());
        final JSONObject jsonObject = new JSONObject(payloadJson);
        final String sub = jwt.getSubject();
        final String lastLogin = jsonObject.optString("custom:lastLogin", "1970-01-01T00:00:00Z");
        final String features = jsonObject.getString("custom:features");
        final String iss = jsonObject.getString("iss");
        final String cognitoUsername = jsonObject.getString("cognito:username");
        final String givenName = jsonObject.getString("given_name");
        final String aud = jsonObject.getString("aud");
        final String eventId = jsonObject.getString("event_id");
        final String tokenUse = jsonObject.getString("token_use");
        final String phoneNumber = jsonObject.getString("custom:phoneNumber");
        final int exp = jsonObject.getInt("exp");
        final int iat = jsonObject.getInt("iat");
        final String familyName = jsonObject.getString("family_name");
        final String email = jsonObject.getString("email");
        final String isAdmin = jsonObject.optString("custom:isAdmin", "");
        return JwtPayload.builder()
                .sub(sub)
                .lastLogin(lastLogin)
                .features(features)
                .iss(iss)
                .cognitoUsername(cognitoUsername)
                .givenName(givenName)
                .aud(aud)
                .eventId(eventId)
                .tokenUse(tokenUse)
                .phoneNumber(phoneNumber)
                .exp(exp)
                .iat(iat)
                .familyName(familyName)
                .email(email)
                .isAdmin(isAdmin)
                .build();
    }

    public JwtPayload decodeTheTokenPayloadInAReadableFormatV2(DecodedJWT jwt) {
        final String payloadJson = decodeBase64ToJson(jwt.getPayload());
        final JSONObject jsonObject = new JSONObject(payloadJson);
        final String sub = jwt.getSubject();
        final String roles = jsonObject.getString("roles");
        final String iss = jsonObject.getString("iss");
        final String aud = jsonObject.getString("aud");
        final int exp = jsonObject.getInt("exp");
        final int iat = jsonObject.getInt("iat");
        final String email = jsonObject.getString("email");
        return JwtPayload.builder()
                .sub(sub)
                .roles(roles)
                .iss(iss)
                .aud(aud)
                .exp(exp)
                .iat(iat)
                .email(email)
                .build();
    }
}
