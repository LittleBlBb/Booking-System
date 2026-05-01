package ru.bookingsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.bookingsystem.service.interfaces.UserService;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
            "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security",
            "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**", "/api/companies/all",
            "/api/register", "/api/activate/*", "/api/login"
    };

    private static final String[] AUTHENTICATION_REQUIRED_URL = {
            "/api/bookings/editBookingById", "/api/bookings/create", "/api/bookings/getById", "/api/bookings/all",
            "/api/companies/create", "/api/me", "/api/resources/findResourceById", "/api/resources/findAll/{companyId}",
            "/api/company/join-request",
    };

    // Здесь должны будут быть ручки для удаления пользователя из компании, принудительная отмена бронирования, просмотр логов по компании.
    private static final String[] ONLY_ADMIN_URL = {
            "/api/requests", "/api/requests/reject", "/api/requests/approve",
    };

//    private static final String[] ONLY_OWNER_URL = {
//            "/api/companies/editCompanyById", "/api/companies/deleteById", "/company/requests", "/requests/reject",
//            "/requests/approve",
//    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider provider){

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(provider)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(WHITE_LIST_URL).permitAll()
//                                .requestMatchers(ONLY_OWNER_URL).hasRole("OWNER")
                                .requestMatchers(ONLY_ADMIN_URL).hasRole("ADMIN")
                                .requestMatchers(AUTHENTICATION_REQUIRED_URL).authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService){

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
}
