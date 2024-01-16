package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.config.UserServiceConfig;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
public class GrantApplicantService {

    private final UserServiceConfig userServiceConfig;
    private final RestTemplate restTemplate;

    @Value("${user-service.domain}")
    private String userServiceDomain;


    private final GrantApplicantRepository grantApplicantRepository;

    public GrantApplicant getApplicantById(final String applicantId) {
        return grantApplicantRepository
                .findByUserId(applicantId)
                .orElseThrow(() -> new NotFoundException(String.format("No Grant Applicant with ID %s was found", applicantId)));
    }

    public String getEmailById(final String applicantId, HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt = header.split(" ")[1];
        String url = userServiceDomain + "/user/" + applicantId + "/email";
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", userServiceConfig.getCookieName() + "=" + jwt);
        final HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }

    public GrantApplicant getApplicantFromPrincipal() {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.getApplicantById(jwtPayload.getSub());
    }

    public GrantApplicant saveApplicant(GrantApplicant applicant) {
        return grantApplicantRepository.save(applicant);
    }
}
