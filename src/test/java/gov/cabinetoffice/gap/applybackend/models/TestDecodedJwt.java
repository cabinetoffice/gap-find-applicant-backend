package gov.cabinetoffice.gap.applybackend.models;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ToString
public class TestDecodedJwt implements DecodedJWT {

    private Date expiresAt;

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getPayload() {
        return null;
    }

    @Override
    public String getSignature() {
        return null;
    }

    @Override
    public String getAlgorithm() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getKeyId() {
        return null;
    }

    @Override
    public Claim getHeaderClaim(String s) {
        return null;
    }

    @Override
    public String getIssuer() {
        return null;
    }

    @Override
    public String getSubject() {
        return null;
    }

    @Override
    public List<String> getAudience() {
        return null;
    }

    @Override
    public Date getExpiresAt() {
        return this.expiresAt;
    }

    @Override
    public Date getNotBefore() {
        return null;
    }

    @Override
    public Date getIssuedAt() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Claim getClaim(String s) {
        return null;
    }

    @Override
    public Map<String, Claim> getClaims() {
        return null;
    }

    public void setExpiresAt(Date expiredAt) {
        this.expiresAt = expiredAt;
    }
}
