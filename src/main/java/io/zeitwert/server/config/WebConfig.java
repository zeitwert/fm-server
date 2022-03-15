package io.zeitwert.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// see https://stackoverflow.com/questions/39331929/spring-catch-all-route-for-index-html
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/{path:\\w+}").setViewName("forward:/");
		registry.addViewController("/**/{path:\\w+}").setViewName("forward:/");
		registry.addViewController("/{path:\\w+}/**{suffix:?!(\\.js|\\.css)$}").setViewName("forward:/");
	}

}
