package com.yury.demo;

import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	@Bean
	public java.util.function.Consumer<KTable<String, String>> transact() {

		return input ->
				input.filter((key, value) -> value.contains("side:true"));


	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
