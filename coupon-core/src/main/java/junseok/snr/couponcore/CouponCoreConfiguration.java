package junseok.snr.couponcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableAutoConfiguration
@ComponentScan
public class CouponCoreConfiguration {
}
