package com.blog.blog.service;
import com.blog.blog.Model.NotificationEmail;
import com.blog.blog.Model.Role;
import com.blog.blog.Model.User;
import com.blog.blog.Model.VerificationToken;
import com.blog.blog.config.AppConfig;
import com.blog.blog.dto.AuthenticationResponse;
import com.blog.blog.dto.LoginRequest;
import com.blog.blog.dto.RefreshTokenRequest;
import com.blog.blog.dto.RegisterRequest;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.repository.RoleRepository;
import com.blog.blog.repository.UserRepository;
import com.blog.blog.repository.VerificationTokenRepository;
import com.blog.blog.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.*;
import java.util.*;

@Service
@Transactional
public class AuthService {

    @Value("${token.expiration.time}")
    private Long tokenExpirationInMillis;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AppConfig appConfig;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, MailService mailService, VerificationTokenRepository verificationTokenRepository, AuthenticationManager authenticationManager, JwtProvider jwtProvider, RefreshTokenService refreshTokenService, AppConfig appConfig,RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.appConfig = appConfig;
        this.roleRepository =roleRepository;
    }
    public void initRoleAndUser() {

        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        adminRole.setRoleDescription("Admin role");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("USER");
        userRole.setRoleDescription("Default role for newly created record");
        roleRepository.save(userRole);

        User adminUser = new User();
        adminUser.setId((long) 1);
        adminUser.setUsername("admin123");
        adminUser.setPassword(passwordEncoder.encode("admin@pass"));
        adminUser.setEmail("srjrocks60@gmail.com");
        adminUser.setEnabled(true);
        adminUser.setCreated(Instant.now());
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        userRepository.save(adminUser);
    }


    public void signup(RegisterRequest registerRequest) {
        User user=new User();
        Role role=roleRepository.findById("USER").get();
        Set<Role> userRoles=new HashSet<>();
        userRoles.add(role);
        user.setRole(userRoles);

        if(registerRequest.getUsername() !=null) {
            user.setUsername((registerRequest.getUsername()));
        }
        if(registerRequest.getEmail() !=null) {
            user.setEmail(registerRequest.getEmail());
        }
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for Signing up to Blog,"+
                "please click on the below url to activate your account:"+
                appConfig.getUrl()+"api/auth/accountVerification/"+token));

    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plusMillis(tokenExpirationInMillis));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken=verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token")));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username=verificationToken.getUser().getUsername();
        User user=userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("User not found with username+ "+username));
        user.setEnabled(true);
        userRepository.save(user);
    }



    public AuthenticationResponse login(LoginRequest loginRequest) {
        //authenticationManager will take Username and password for generating token
        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
               loginRequest.getPassword()));
        //Before Generating Token We have to store authenticate object to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        //Now these our Authentication Manager Verifies our credentials at Background,if they are matching they return object called authenticate
        String token=jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }

    @Transactional
    public User getCurrentUser() {
       org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
               getContext().getAuthentication().getPrincipal();

        return userRepository.findByUsername(principal.getUsername())
        .orElseThrow(()->new UsernameNotFoundException("User name not found-"+principal.getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());//If token is invalid then there is run time exception
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();

    }


}


