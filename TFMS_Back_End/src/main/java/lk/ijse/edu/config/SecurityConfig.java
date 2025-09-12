package lk.ijse.edu.config;

import lk.ijse.edu.util.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth->
                                auth.requestMatchers(
                                                "/auth/login",
                                                "/auth/register/customer",
                                                "/auth/register/supplier",
//                                "/auth/saveAdmin",
                                                "/teaMaker/register",
                                                "/teaMaker/update",
                                                "/teaMaker/delete",
                                                "/teaMaker/getAll",
                                                "/teaMaker/search/*",
                                                "/teaMaker/changeStatus/*",
                                                "/stockManager/**",
                                                "/financeManager/**",
                                                "/salesManager/**",
                                                "/teaCard/**",
                                                "/teaProduct/**",
                                                "/customer/**",
                                                "/supplier/**",
                                                "/teaMakerDashboard/***",
                                                "/teaMakerDashboard/getSupplierByCard/*"
                                        ).permitAll()
                                        .anyRequest().authenticated())
                .sessionManagement(
                        session->
                                session.sessionCreationPolicy
                                        (SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider
                = new DaoAuthenticationProvider();
        daoAuthenticationProvider
                .setUserDetailsService(userDetailsService);
        daoAuthenticationProvider
                .setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;

    }
}
