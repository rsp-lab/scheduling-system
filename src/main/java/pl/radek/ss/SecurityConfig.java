package pl.radek.ss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	AppConfig appConfig;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http.csrf().ignoringAntMatchers("/h2/**", "/api/**");
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin(); // dla konsoli H2
        // Powiedz przeglądarce, aby blokowała wszystko co wygląda na XSS, działa na reflected XSS tylko. Można pokombinować
        //  też z contentSecurityPolicy jeśli jakieś przeglądarki nagłówka XSS PROTECTION nie obsługują
        // http.headers().xssProtection(); // .and().contentSecurityPolicy("script-src 'self'"); nie działa javascript dla naszej apki (np. H2, przyciski)
        http
                .authorizeRequests()
                
                .antMatchers("/favicon.ico", "/views/includes/css/**", "/views/includes/js/**", "/rest/**").permitAll()
                .antMatchers("/", "/rest/**", "/h2/**", "/css/**").permitAll()
                
                .antMatchers("/**").permitAll()
                
                // TODO security
                /*
                .antMatchers("/start.jsp").permitAll()
                .antMatchers("/registerAccount").permitAll()
                
                .antMatchers("/user/page").hasAnyRole("ROLE_USER")
                .antMatchers("/user/schedule").hasAnyRole("ROLE_USER")
                .antMatchers("/downloadSchedule").hasAnyRole("ROLE_USER")
                .antMatchers("/saveSchedule").hasAnyRole("ROLE_USER")
                .antMatchers("/views/user/page.jsp").hasAnyRole("ROLE_USER")
                
                .antMatchers("/account/add").hasAnyRole("ROLE_ADMIN")
                .antMatchers("/administrator/page").hasAnyRole("ROLE_ADMIN")
                .antMatchers("/views/administrator/page.jsp").hasAnyRole("ROLE_ADMIN")
                .antMatchers("/findAccount").hasAnyRole("ROLE_ADMIN")
                .antMatchers("/deleteAccount").hasAnyRole("ROLE_ADMIN")
                
                .antMatchers("/saveMessage").permitAll()
                .antMatchers("/manageEvent").permitAll()
                .antMatchers("/createEvent").permitAll()
                .antMatchers("/deleteEvent").permitAll()
                .antMatchers("/detailsAccount").hasAnyRole("ROLE_USER, ROLE_ADMIN")
                .antMatchers("/detailsEvent").permitAll()
                .antMatchers("/findEvent").permitAll()
                .antMatchers("/getMessageQuery").permitAll()
                .antMatchers("/saveMessageQuery").permitAll()
                */
                
                .and()
                    .formLogin(form -> form
                            .loginPage("/start")
                            // .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/start", true)
                            .failureForwardUrl("/start?error=true")
                            .permitAll())
                    .rememberMe(remember -> remember
                            .key("uniqueKEY")
                            .tokenValiditySeconds(604800)) // 7 dni (w sekundach)
                    .logout(logout -> logout
                            .logoutSuccessUrl("/start")
                            .permitAll());
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication().dataSource(appConfig.hikariDataSource())
                .usersByUsernameQuery("SELECT username, password, enabled FROM account WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT a.username, r.name FROM account a JOIN role r ON a.id = r.user_id WHERE a.username = ?");
    }
    
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }
}