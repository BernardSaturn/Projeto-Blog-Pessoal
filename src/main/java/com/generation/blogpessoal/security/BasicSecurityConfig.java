package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Indica que esta é uma classe de configuração
@EnableWebSecurity // Habilita a segurança web do Spring Security
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter; // Injeta o filtro de autenticação JWT

    @Bean
    UserDetailsService userDetailsService() {
        // Define um bean para o serviço de detalhes do usuário
        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // Define um bean para o codificador de senhas usando BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        // Define um bean para o provedor de autenticação
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService()); // Configura o serviço de detalhes do usuário
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // Configura o codificador de senhas
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        // Define um bean para o gerenciador de autenticação
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configura a cadeia de filtros de segurança

        http
            .sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define a política de criação de sessão como STATELESS
            .csrf(csrf -> csrf.disable()) // Desabilita a proteção CSRF
            .cors(withDefaults()); // Habilita CORS com as configurações padrão

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/usuarios/logar").permitAll() // Permite acesso público ao endpoint de login
                .requestMatchers("/usuarios/cadastrar").permitAll() // Permite acesso público ao endpoint de cadastro
                .requestMatchers("/error/**").permitAll() // Permite acesso público aos endpoints de erro
                .requestMatchers(HttpMethod.OPTIONS).permitAll() // Permite acesso público às requisições OPTIONS
                .anyRequest().authenticated()) // Exige autenticação para qualquer outra requisição
            .authenticationProvider(authenticationProvider()) // Configura o provedor de autenticação
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro JWT antes do filtro de autenticação padrão
            .httpBasic(withDefaults()); // Habilita a autenticação HTTP Basic com as configurações padrão

        return http.build(); // Constrói e retorna a configuração de segurança
    }

}
