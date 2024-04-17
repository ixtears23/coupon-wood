package junseok.snr.couponcore.service;

import junseok.snr.couponcore.repository.redis.RedisRepository;
import junseok.snr.couponcore.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {
    private final RedisRepository redisRepository;

    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }

        final String key = CouponRedisUtils.getIssueRequestKey(couponId);
        final Long issueQuantity = redisRepository.sCard(key);

        return totalQuantity > issueQuantity;
    }


    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}
