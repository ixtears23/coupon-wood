package junseok.snr.couponcore.service;

import junseok.snr.couponcore.model.Coupon;
import junseok.snr.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {
    private final CouponIssueService couponIssueService;

    public CouponRedisEntity getCouponCache(long couponId) {
        final Coupon coupon = couponIssueService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }
}
