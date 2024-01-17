package gov.cabinetoffice.gap.applybackend.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.config.UserServiceConfig;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private UserServiceConfig userServiceConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JwtService serviceUnderTest;


    @BeforeEach
    void setup() {
        userServiceConfig = UserServiceConfig.builder()
                .domain("http://localhost:8082")
                .cookieName("user-service-token")
                .build();

        serviceUnderTest = new JwtService(userServiceConfig, restTemplate);
    }

    @Test
    void isTokenExpired_returnTrue() {
        final String encodedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3NWFiNWZiZC0wNjgyLTRkM2QtYTQ2Ny0wMWM3YTQ0N2YwN2MiLCJjdXN0b206ZmVhdHVyZXMiOiJ0ZXN0ZXIiLCJpc3MiOiJhc2VydmljZXByb3ZpZGVyIiwiY29nbml0bzp1c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsImF1ZCI6ImxrYXNqZGxramRzbGtqc2QiLCJldmVudF9pZCI6ImxramRsa2pzZmxraiIsInRva2VuX3VzZSI6ImlkIiwiY3VzdG9tOnBob25lTnVtYmVyIjoiMDAwMDAwMDAwMDAwMCIsImF1dGhfdGltZSI6MTY2MTQxODk5MywiZXhwIjoxNjYxNDIyNTkzLCJpYXQiOjE2NjE0MTg5OTMsImZhbWlseV9uYW1lIjoiVGVzdGVyIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.qIlN7ZFSUwVpTfGr6rL0e6koDPEezS0O__8iZnMRm_w";
        final DecodedJWT decodedJWT = serviceUnderTest.decodedJwt(encodedJwt);
        final boolean methodResponse = serviceUnderTest.isTokenExpired(decodedJWT);
        assertTrue(methodResponse);
    }

    @Test
    void isTokenExpired_returnFalse() {
        final String encodedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3NWFiNWZiZC0wNjgyLTRkM2QtYTQ2Ny0wMWM3YTQ0N2YwN2MiLCJjdXN0b206ZmVhdHVyZXMiOiJ0ZXN0ZXIiLCJpc3MiOiJhc2VydmljZXByb3ZpZGVyIiwiY29nbml0bzp1c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsImF1ZCI6ImxrYXNqZGxramRzbGtqc2QiLCJldmVudF9pZCI6ImxramRsa2pzZmxraiIsInRva2VuX3VzZSI6ImlkIiwiY3VzdG9tOnBob25lTnVtYmVyIjoiMDAwMDAwMDAwMDAwMCIsImF1dGhfdGltZSI6MTY2MTQxODk5MywiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjE2NjE0MTg5OTMsImZhbWlseV9uYW1lIjoiVGVzdGVyIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.VaOO8zYWdtSkyPCizyOvTX9CzxRuePZcLC9Yyxi3Baw";
        final DecodedJWT decodedJWT = serviceUnderTest.decodedJwt(encodedJwt);
        final boolean methodResponse = serviceUnderTest.isTokenExpired(decodedJWT);
        assertFalse(methodResponse);
    }

    @Test
    void verifyToken_ReturnsTrue() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.of(Optional.of(Boolean.TRUE)));

        final boolean response = serviceUnderTest.verifyToken("testToken");

        assertTrue(response);
    }

    @Test
    void verifyToken_ReturnsFalse() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.of(Optional.of(Boolean.FALSE)));

        final boolean response = serviceUnderTest.verifyToken("testToken");

        assertFalse(response);
    }

    @Test
    void verifyToken_CallsUserService() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", userServiceConfig.getCookieName() + "=" + "testToken");
        final HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.of(Optional.of(Boolean.FALSE)));

        serviceUnderTest.verifyToken("testToken");

        verify(restTemplate).exchange(userServiceConfig.getDomain() + "/is-user-logged-in", HttpMethod.GET, requestEntity, Boolean.class);
    }

    @Test
    void get_DecodeData() {
        final String encodedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3NWFiNWZiZC0wNjgyLTRkM2QtYTQ2Ny0wMWM3YTQ0N2YwN2MiLCJjdXN0b206ZmVhdHVyZXMiOiJ0ZXN0ZXIiLCJpc3MiOiJhc2VydmljZXByb3ZpZGVyIiwiY29nbml0bzp1c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsImF1ZCI6ImxrYXNqZGxramRzbGtqc2QiLCJldmVudF9pZCI6ImxramRsa2pzZmxraiIsInRva2VuX3VzZSI6ImlkIiwiY3VzdG9tOnBob25lTnVtYmVyIjoiMDAwMDAwMDAwMDAwMCIsImF1dGhfdGltZSI6MTY2MTQxODk5MywiZXhwIjoxNjYxNDIyNTkzLCJpYXQiOjE2NjE0MTg5OTMsImZhbWlseV9uYW1lIjoiVGVzdGVyIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.qIlN7ZFSUwVpTfGr6rL0e6koDPEezS0O__8iZnMRm_w";
        final DecodedJWT methodResponse = serviceUnderTest.decodedJwt(encodedJwt);

        assertThat(methodResponse.getClaim("sub").asString()).isEqualTo("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
        assertThat(methodResponse.getClaim("custom:features").asString()).isEqualTo("tester");
        assertThat(methodResponse.getClaim("iss").asString()).isEqualTo("aserviceprovider");
        assertThat(methodResponse.getClaim("cognito:username").asString()).isEqualTo("test@test.com");
        assertThat(methodResponse.getClaim("given_name").asString()).isEqualTo("Test");
        assertThat(methodResponse.getClaim("aud").asString()).isEqualTo("lkasjdlkjdslkjsd");
        assertThat(methodResponse.getClaim("event_id").asString()).isEqualTo("lkjdlkjsflkj");
        assertThat(methodResponse.getClaim("token_use").asString()).isEqualTo("id");
        assertThat(methodResponse.getClaim("custom:phoneNumber").asString()).isEqualTo("0000000000000");
        assertThat(methodResponse.getClaim("auth_time").asLong()).isEqualTo(1661418993);
        assertThat(methodResponse.getClaim("iat").asLong()).isEqualTo(1661418993);
        assertThat(methodResponse.getClaim("exp").asLong()).isEqualTo(1661422593);
        assertThat(methodResponse.getClaim("family_name").asString()).isEqualTo("Tester");
        assertThat(methodResponse.getClaim("email").asString()).isEqualTo("test@test.com");
    }

    @Test
    void decodeTheTokenPayloadInAReadableFormat() throws JSONException {
        final String jwtPayloadConvertedToJson = "{\"sub\":\"b36add80-f621-42d1-8914-0391c37d4b8e\",\"custom:lastLogin\":\"2022-09-01T15:24:48.610Z\",\"custom:features\":\"administrator,ordinary_user\",\"iss\":\"https:\\/\\/cognito-idp.eu-west-2.amazonaws.com\\/eu-west-2_VWWYnYQpH\",\"cognito:username\":\"iain.cooper@cabinetoffice.gov.uk\",\"given_name\":\"Iain\",\"aud\":\"7o96o19m5927s29jhq259f9l1i\",\"event_id\":\"a3607c45-f8b3-438e-8723-31f2fcceb00d\",\"token_use\":\"id\",\"custom:phoneNumber\":\"+447525806624\",\"auth_time\":1662047247,\"exp\":1662050847,\"iat\":1662047247,\"family_name\":\"Cooper\",\"email\":\"iain.cooper@cabinetoffice.gov.uk\",\"custom:isAdmin\":\"true\"}";
        final JSONObject jsonObject = new JSONObject(jwtPayloadConvertedToJson);
        final String sub = jsonObject.getString("sub");
        final String lastLogin = jsonObject.getString("custom:lastLogin");
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
        final String isAdmin = jsonObject.getString("custom:isAdmin");
        final String token = "eyJraWQiOiJoRG5mWTR1QVpjS3Z5TlZVNFZVN0ZvRU41STBCT0FpQVwvRTU3Yll6cHY4Zz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiMzZhZGQ4MC1mNjIxLTQyZDEtODkxNC0wMzkxYzM3ZDRiOGUiLCJjdXN0b206bGFzdExvZ2luIjoiMjAyMi0wOS0wMVQxNToyNDo0OC42MTBaIiwiY3VzdG9tOmZlYXR1cmVzIjoiYWRtaW5pc3RyYXRvcixvcmRpbmFyeV91c2VyIiwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXdlc3QtMi5hbWF6b25hd3MuY29tXC9ldS13ZXN0LTJfVldXWW5ZUXBIIiwiY29nbml0bzp1c2VybmFtZSI6ImlhaW4uY29vcGVyQGNhYmluZXRvZmZpY2UuZ292LnVrIiwiZ2l2ZW5fbmFtZSI6IklhaW4iLCJhdWQiOiI3bzk2bzE5bTU5MjdzMjlqaHEyNTlmOWwxaSIsImV2ZW50X2lkIjoiYTM2MDdjNDUtZjhiMy00MzhlLTg3MjMtMzFmMmZjY2ViMDBkIiwidG9rZW5fdXNlIjoiaWQiLCJjdXN0b206cGhvbmVOdW1iZXIiOiIrNDQ3NTI1ODA2NjI0IiwiYXV0aF90aW1lIjoxNjYyMDQ3MjQ3LCJleHAiOjE2NjIwNTA4NDcsImlhdCI6MTY2MjA0NzI0NywiZmFtaWx5X25hbWUiOiJDb29wZXIiLCJlbWFpbCI6ImlhaW4uY29vcGVyQGNhYmluZXRvZmZpY2UuZ292LnVrIiwiY3VzdG9tOmlzQWRtaW4iOiJ0cnVlIn0.UjYTAxgYs_VJ4o5kZJa9Ib1FQx6a9ngQZg1diAiUFsAUBWeY2y4gY9KkHC7OzvS78ZsdM00kCq0zb2zulm2POZ7xpFx4AWmKSjClrGfUP5Fc4bSsjEBxk1h9Gn-FRgCeCipY3xuaJc0_T2F4KRWGkecuxsNhD718NrOvP38UCmCUcfpEgoEdqH7Bl2eIdUWR12rEDfN6YQuw78baCJpsPUptcS2zzdPrQb840uJWB5D_MjwegsBrf9_MGElrpplzZ2Hf1WMF46Y_5mgt8QtNzjXhg-TVKTlW6ZaFtRR4R8VGoF4xtEPizNYVqGka9ph6MmQlBHkLONVxnzl542jLOw";
        final DecodedJWT decodedJWT = serviceUnderTest.decodedJwt(token);
        final JwtPayload result = serviceUnderTest.decodeTheTokenPayloadInAReadableFormat(decodedJWT);

        final JwtPayload expected = JwtPayload.builder()
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
        assertEquals(expected, result);
    }
}
