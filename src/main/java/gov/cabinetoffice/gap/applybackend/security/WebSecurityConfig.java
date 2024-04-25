package gov.cabinetoffice.gap.applybackend.security;

import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantRepository;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtService jwtService;
    private final GrantApplicantRepository grantApplicantRepository;
    private final GrantApplicantService grantApplicantService;
    private final GrantApplicantOrganisationProfileRepository grantApplicantOrganisationProfileRepository;

    @Value("${feature.onelogin.enabled}")
    private boolean oneLoginEnabled;

    /**
     * Using WebSecurityCustomizer#ignoring triggers a warning at app start-up for each path ignored.
     * Unfortunately, its recommendation is not suitable for us, since we not only need authentication to be ignored
     * but also the JWT filter - permitAll via HttpSecurity#authorizeHttpRequests will still trigger the JwtTokenFilter
     * One alternative is to create two SecurityFilterChain beans, one for public paths and another for secured.
     * But the secured path then cannot use anyRequest(): you must specify every path you want authenticated.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // specify any paths you don't want subject to JWT validation/authentication
        return web -> web.ignoring().mvcMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/health",
                "/grant-applicant/register",
                "/submissions/{submissionId}/question/{questionId}/attachment/scanresult",
                "/grant-adverts/{advertSlug}/scheme-version",
                "/jwt/isValid");
    }

    @Bean
    public SecurityFilterChain filterChainPublic(HttpSecurity http) throws Exception {
        // disable session creation by Spring Security, since auth will happen on every request
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

        // any requests to any path must be authenticated (unless specified in webSecurityCustomizer bean)
        http
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated();

        if (oneLoginEnabled) {
            http.addFilterBefore(
                    new JwtTokenFilterV2(
                            jwtService,
                            grantApplicantRepository,
                            grantApplicantService,
                            grantApplicantOrganisationProfileRepository
                    ),
                    UsernamePasswordAuthenticationFilter.class
            );
        } else {
            http.addFilterBefore(
                    new JwtTokenFilter(jwtService),
                    UsernamePasswordAuthenticationFilter.class
            );
        }

        // disable a bunch of Spring Security default stuff we don't need
        http
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .csrf().disable();

        // handle exceptions when non-auth'd user hits an endpoint
        http
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        return http.build();
    }

}