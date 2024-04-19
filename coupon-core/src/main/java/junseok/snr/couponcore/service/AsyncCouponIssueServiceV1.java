package junseok.snr.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junseok.snr.couponcore.component.DistributeLockExecutor;
import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import junseok.snr.couponcore.repository.redis.RedisRepository;
import junseok.snr.couponcore.repository.redis.dto.CouponIssueRequest;
import junseok.snr.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {
    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        final CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();

        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3_000, 3_000, () -> {
            couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
            issueRequest(couponId, userId);
        });
    }

    private void issueRequest(long couponId, long userId) {
        final CouponIssueRequest couponIssueRequest = new CouponIssueRequest(couponId, userId);

        try {
            final String value = objectMapper.writeValueAsString(couponIssueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(couponIssueRequest));
        }

    }

}
