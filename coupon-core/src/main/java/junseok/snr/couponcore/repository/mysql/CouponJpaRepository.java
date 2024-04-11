package junseok.snr.couponcore.repository.mysql;

import junseok.snr.couponcore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository <Coupon, Long > {
}
