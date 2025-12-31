package io.zeitwert.fm.server.config.security

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// see https://stackoverflow.com/questions/39331929/spring-catch-all-route-for-index-html
@Configuration
open class WebMvcConfiguration : WebMvcConfigurer {

	override fun addViewControllers(registry: ViewControllerRegistry) {
		registry.addViewController("/{path:\\w+}").setViewName("forward:/index.html")
		registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}").setViewName("forward:/index.html")
	}

}
