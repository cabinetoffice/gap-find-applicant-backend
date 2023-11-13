package gov.cabinetoffice.gap.applybackend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantRepository;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * This class cannot be a Spring bean, otherwise Spring will automatically apply it to all
 * requests, regardless of whether they've been specifically ignored
 */
@RequiredArgsConstructor
public class JwtTokenFilterV2 extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final GrantApplicantRepository grantApplicantRepository;
    private final GrantApplicantService grantApplicantService;
    private final GrantApplicantOrganisationProfileRepository grantApplicantOrganisationProfileRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Check if auth header exists. If not, return without setting authentication in the security context
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        //verify the token
        String normalisedJwt = header.split(" ")[1];
        if (!jwtService.verifyToken(normalisedJwt)) {
            chain.doFilter(request, response);
            return;
        }

        DecodedJWT decodedJWT = jwtService.decodedJwt(normalisedJwt);
        //set the Security context, so we can access it everywhere
        JwtPayload jwtPayload = jwtService.decodeTheTokenPayloadInAReadableFormatV2(decodedJWT);

        if (!jwtPayload.getRoles().contains("APPLICANT")) {
            throw new ForbiddenException("User is not an applicant");
        }

        final boolean grantApplicantExists = grantApplicantRepository.existsByUserId(jwtPayload.getSub());
        if (!grantApplicantExists) {
            createNewApplicant(jwtPayload.getSub());
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private void createNewApplicant(final String sub) {
        final GrantApplicant grantApplicant = grantApplicantRepository.save(GrantApplicant.builder().userId(sub).build());
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile
                .builder()
                .build();
        profile.setApplicant(grantApplicant);

        final GrantApplicantOrganisationProfile savedProfile = grantApplicantOrganisationProfileRepository.save(profile);
        grantApplicant.setOrganisationProfile(savedProfile);

        grantApplicantService.saveApplicant(grantApplicant);
    }
}
