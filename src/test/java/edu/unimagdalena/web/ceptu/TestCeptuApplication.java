package edu.unimagdalena.web.ceptu;

import org.springframework.boot.SpringApplication;

public class TestCeptuApplication {

	public static void main(String[] args) {
		SpringApplication.from(CeptuApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
