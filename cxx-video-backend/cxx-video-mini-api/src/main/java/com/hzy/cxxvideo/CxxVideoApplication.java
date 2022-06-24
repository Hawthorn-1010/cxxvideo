package com.hzy.cxxvideo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = {"com.hzy.cxxvideo", "org.n3r.idworker"})
@MapperScan("com.hzy.cxxvideo.mapper")
public class CxxVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CxxVideoApplication.class, args);
	}

}
