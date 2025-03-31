package pl.radek.ss;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@EnableCaching
public class AppConfig implements WebMvcConfigurer
{
    @Autowired
    private Environment environment;

    @Bean
    // @ConfigurationProperties("hikari.datasource")
    public HikariDataSource hikariDataSource() {
        DataSourceBuilder<HikariDataSource> dataSource = DataSourceBuilder.create().type(HikariDataSource.class);
        dataSource.url(environment.getProperty("spring.datasource.url"));
        dataSource.username(environment.getProperty("spring.datasource.username"));
        dataSource.password(environment.getProperty("spring.datasource.password"));
        return dataSource.build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60);
        return messageSource;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
