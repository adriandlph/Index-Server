package com.adlph.internal.managment.index.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class IndexServerApplication {

	public static void main(String[] args) {
		var baseDir = Paths.get("").toAbsolutePath().normalize();
		var dirs = new String[]{"db", "logs"};
		for (var dir : dirs) {
			var path = baseDir.resolve(dir);
			if (Files.notExists(path)) {
				try {
					Files.createDirectories(path);
				} catch (Exception e) {
					System.err.println("Warning: Could not create directory " + path + ": " + e.getMessage());
				}
			}
		}
		SpringApplication.run(IndexServerApplication.class, args);
	}

}
