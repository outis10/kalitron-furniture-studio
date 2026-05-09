package com.kalitron.studio.web.rest.custom;

import static com.kalitron.studio.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.kalitron.studio.security.SecurityUtils.JWT_ALGORITHM;
import static com.kalitron.studio.security.SecurityUtils.USER_ID_CLAIM;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kalitron.studio.domain.Authority;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.domain.UserSocialProvider;
import com.kalitron.studio.repository.AuthorityRepository;
import com.kalitron.studio.repository.UserRepository;
import com.kalitron.studio.repository.UserSocialProviderRepository;
import com.kalitron.studio.security.AuthoritiesConstants;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.security.RandomUtil;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthResource {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleAuthResource.class);
    private static final String PROVIDER_GOOGLE = "GOOGLE";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:86400}")
    private long tokenValidityInSeconds;

    private final UserRepository userRepository;
    private final UserSocialProviderRepository socialProviderRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public GoogleAuthResource(
        UserRepository userRepository,
        UserSocialProviderRepository socialProviderRepository,
        AuthorityRepository authorityRepository,
        PasswordEncoder passwordEncoder,
        JwtEncoder jwtEncoder
    ) {
        this.userRepository = userRepository;
        this.socialProviderRepository = socialProviderRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public record GoogleLoginRequest(String credential) {}

    public record JwtResponse(String token) {}

    @PostMapping("/google")
    @Transactional
    public ResponseEntity<JwtResponse> authenticateWithGoogle(@RequestBody GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = verifyToken(request.credential());
        if (payload == null) {
            return ResponseEntity.badRequest().build();
        }

        String sub = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String picture = (String) payload.get("picture");

        LOG.debug("Google SSO login for email: {}", email);

        Optional<UserSocialProvider> existingProvider = socialProviderRepository.findByProviderAndProviderId(PROVIDER_GOOGLE, sub);

        User user;
        if (existingProvider.isPresent()) {
            UserSocialProvider sp = existingProvider.get();
            sp.setEmail(email);
            sp.setImageUrl(picture);
            socialProviderRepository.save(sp);
            user = sp.getUser();
            if (picture != null && !picture.equals(user.getImageUrl())) {
                user.setImageUrl(picture);
                userRepository.save(user);
            }
        } else {
            user = userRepository.findOneByEmailIgnoreCase(email).orElseGet(() -> createNewUser(email, firstName, lastName, picture));

            UserSocialProvider sp = new UserSocialProvider();
            sp.setUser(user);
            sp.setProvider(PROVIDER_GOOGLE);
            sp.setProviderId(sub);
            sp.setEmail(email);
            sp.setImageUrl(picture);
            sp.setCreatedDate(Instant.now());
            socialProviderRepository.save(sp);
        }

        String authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.joining(" "));
        String jwt = createToken(user.getLogin(), authorities, user.getId());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    private String createToken(String login, String authorities, Long userId) {
        Instant now = Instant.now();
        Instant validity = now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(login)
            .claim(AUTHORITIES_CLAIM, authorities)
            .claim(USER_ID_CLAIM, userId)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private GoogleIdToken.Payload verifyToken(String credential) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
            GoogleIdToken idToken = verifier.verify(credential);
            return idToken != null ? idToken.getPayload() : null;
        } catch (GeneralSecurityException | IOException e) {
            LOG.error("Failed to verify Google ID token: {}", e.getMessage());
            return null;
        }
    }

    private User createNewUser(String email, String firstName, String lastName, String picture) {
        User newUser = new User();
        String login = email.substring(0, email.indexOf("@")).toLowerCase();
        if (userRepository.findOneByLogin(login).isPresent()) {
            login = login + "-" + RandomUtil.generateActivationKey().substring(0, 5);
        }
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(RandomUtil.generatePassword()));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email.toLowerCase());
        newUser.setImageUrl(picture);
        newUser.setActivated(true);
        newUser.setLangKey("es");

        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        return newUser;
    }
}
