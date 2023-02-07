package gov.cabinetoffice.gap.applybackend.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.config.CognitoConfigProperties;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.JwkNotValidTokenException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

@RequiredArgsConstructor
@Service
public class JwtService {
    private final CognitoConfigProperties cognitoProps;

    public DecodedJWT decodedJwt(String normalisedJWT) {
        return JWT.decode(normalisedJWT);
    }

    public boolean verifyToken(DecodedJWT jwt) throws JwkException {
        if (isTokenExpired(jwt)) {
            return false;
        }

        boolean isExpectedIssuer = jwt.getIssuer().equals(cognitoProps.getDomain());
        boolean isExpectedAud = jwt.getAudience().get(0).equals(cognitoProps.getAppClientId());
        if (!isExpectedAud || !isExpectedIssuer) {
            throw new JwkNotValidTokenException("Token is not valid");
        }
        JwkProvider provider = new UrlJwkProvider(cognitoProps.getDomain());
        Jwk jwk = provider.get(jwt.getKeyId());
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
        algorithm.verify(jwt);

        return true;
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
        final int authTime = jsonObject.getInt("auth_time");
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
                .authTime(authTime)
                .exp(exp)
                .iat(iat)
                .familyName(familyName)
                .email(email)
                .isAdmin(isAdmin)
                .build();
    }
}
