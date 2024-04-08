package junseok.snr.couponconsumer;

import junseok.snr.couponcore.CouponConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(CouponConfiguration.class)
@SpringBootApplication
public class CouponConsumerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core, application-consumer");
        SpringApplication.run(CouponConfiguration.class, args);
    }
}
