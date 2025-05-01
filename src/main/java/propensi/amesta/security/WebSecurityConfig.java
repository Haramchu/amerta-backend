package propensi.amesta.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import propensi.amesta.security.jwt.JwtTokenFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/user/**").hasAnyAuthority("administrasi", "direktur")

                .requestMatchers("/api/barang/add").hasAnyAuthority("direktur", "general_manager")
                .requestMatchers("/api/barang/update/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang")
                .requestMatchers("/api/barang/change-status/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang")
                .requestMatchers("/api/barang/viewall").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/barang/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/barang/transfer").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "administrasi")
                .requestMatchers("/api/barang/transfer/viewall").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "administrasi", "komisaris")
                .requestMatchers("/api/barang/transfer/view/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "administrasi", "komisaris")

                .requestMatchers("/api/penerimaan/create").hasAnyAuthority("direktur", "administrasi")
                .requestMatchers("/api/penerimaan/viewall").hasAnyAuthority("direktur", "komisaris")
                .requestMatchers("/api/penerimaan/view/{id}").hasAnyAuthority("direktur", "komisaris")

                .requestMatchers("/api/pengeluaran/create").hasAnyAuthority("direktur", "administrasi")
                .requestMatchers("/api/pengeluaran/viewall").hasAnyAuthority("direktur", "komisaris")
                .requestMatchers("/api/pengeluaran/view/{id}").hasAnyAuthority("direktur", "komisaris")

                .requestMatchers("/api/gudang/add").hasAnyAuthority("direktur", "general_manager")
                .requestMatchers("/api/gudang/").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "administrasi", "komisaris")
                .requestMatchers("/api/gudang/{namaGudang}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "administrasi", "komisaris")
                .requestMatchers("/api/gudang/update/{namaGudang}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang")
                
                // TODO: implement security untuk add customer.

                .requestMatchers("/api/purchase-order/add").hasAnyAuthority("direktur", "sales", "general_manager", "administrasi")
                .requestMatchers("/api/purchase-order/").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/purchase-order/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/purchase-order/confirm/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/purchase-order/payment/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/purchase-order/delivery/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/purchase-order/complete-delivery/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")

                .requestMatchers("/api/sales-order/add").hasAnyAuthority("direktur", "sales", "general_manager", "administrasi")
                .requestMatchers("/api/sales-order/viewall").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/sales-order/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/sales-order/confirm/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/sales-order/payment/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/sales-order/shipping/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")
                .requestMatchers("/api/sales-order/confirm-shipping/{id}").hasAnyAuthority("direktur", "general_manager", "sales", "administrasi")

                .requestMatchers("/api/shipping/create").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi")
                .requestMatchers("/api/shipping/generate/purchase-order/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi")
                .requestMatchers("/api/shipping/generate/sales-order/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi")
                .requestMatchers("/api/shipping/").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/shipping/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/shipping/search").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .requestMatchers("/api/shipping/update-status/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi")
                .requestMatchers("/api/shipping/update/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi")
                .requestMatchers("/api/shipping/delete/{id}").hasAnyAuthority("direktur", "general_manager")
                .requestMatchers("/api/shipping/export-pdf/{id}").hasAnyAuthority("direktur", "general_manager", "kepala_gudang", "sales", "administrasi", "komisaris")
                .anyRequest().authenticated()    
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .defaultSuccessUrl("/")
            )

            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler())
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> e
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
                .accessDeniedHandler(
                    new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response,
                                AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Anda Tidak Memiliki Akses ke Endpoint Ini!");
                        }
                    }
                )
            );
                    
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }   

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
                    throws IOException, ServletException {
                response.sendRedirect("/access-denied");
            }
        };
    }
}