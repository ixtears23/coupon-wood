package junseok.snr.couponapi;

import junseok.snr.couponcore.CouponConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(CouponConfiguration.class)
@SpringBootApplication
public class CouponApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core, application-api");
        SpringApplication.run(CouponConfiguration.class, args);
    }
}
