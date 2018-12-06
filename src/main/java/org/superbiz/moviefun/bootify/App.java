package org.superbiz.moviefun.bootify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.superbiz.moviefun.ActionServlet;

@SpringBootApplication
@ComponentScan(basePackages= {"org.superbiz.moviefun"})
@EntityScan(basePackages = {"org.superbiz.moviefun"})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public ServletRegistrationBean getServletRegistrationBean(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }
}
