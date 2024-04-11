package junseok.snr.couponcore.repository.mysql;

import junseok.snr.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository <CouponIssue, Long > {
}
