package io.dddrive.test

import org.springframework.boot.SpringApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@ServletComponentScan
@Configuration
@EnableAsync
@ComponentScan("io.dddrive")
open class TestApplication {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(TestApplication::class.java, *args)
		}
	}
}
