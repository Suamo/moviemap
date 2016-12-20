package hello;

import hello.storage.StorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    StorageService getStorageService() {
        return new StorageService();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}