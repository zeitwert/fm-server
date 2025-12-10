package io.zeitwert.test

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication(exclude = [CodecsAutoConfiguration::class])
@EnableAsync
@ComponentScan("io.zeitwert", "io.dddrive.core")
@Profile("test", "ci")
open class TestApplication {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(TestApplication::class.java, *args)
		}
	}
}
