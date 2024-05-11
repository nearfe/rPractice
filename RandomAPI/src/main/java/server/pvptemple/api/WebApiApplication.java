package server.pvptemple.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("server.pvptemple.api.model")
@ComponentScan({ "server.pvptemple.api.repo", "server.pvptemple.api.model", "server.pvptemple.api.controller" })
public class WebApiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WebApiApplication.class, args);
	}

}
