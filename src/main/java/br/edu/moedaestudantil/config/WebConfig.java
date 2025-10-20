package br.edu.moedaestudantil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }
    
    // Configuração de segurança temporariamente desabilitada para facilitar desenvolvimento
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http.csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(authz -> authz
    //             .requestMatchers("/h2-console/**", "/", "/css/**", "/alunos/**", "/empresas/**").permitAll()
    //             .anyRequest().authenticated()
    //         )
    //         .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
    //         .formLogin(form -> form.permitAll());
    //     return http.build();
    // }
}
