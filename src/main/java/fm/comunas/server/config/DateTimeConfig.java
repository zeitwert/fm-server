package fm.comunas.server.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DateTimeConfig implements WebMvcConfigurer {

	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
		// registrar.setUseIsoFormat(true);
		registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		registrar.registerFormatters(registry);
	}

}