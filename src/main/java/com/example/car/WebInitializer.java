package com.example.car;

import com.example.car.client.PurgoMalumConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        // Spring 애플리케이션 컨텍스트 생성 및 설정
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.register(ApplicationConfig.class);
        context.setServletContext(servletContext);

        // DispatcherServlet 등록 및 매핑
        final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}