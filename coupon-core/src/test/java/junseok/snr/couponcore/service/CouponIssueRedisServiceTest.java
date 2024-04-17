package junseok.snr.couponcore.service;

import junseok.snr.couponcore.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestKey;

class CouponIssueRedisServiceTest extends TestConfig {

    @Autowired
    private CouponIssueRedisService sut;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        final Collection<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true를 반환한다")
    void availableTotalIssueQuantity_1() {
        int totalIssueQuantity = 10;
        long couponId = 1;

        final boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false를 반환한다")
    void availableTotalIssueQuantity_2() {
        int totalIssueQuantity = 10;
        long couponId = 1;
        IntStream.range(0, totalIssueQuantity)
                .forEach(userId -> {
                    redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));
                });

        final boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true를 반환한다")
    void availableUserIssueQuantity_1() {

        long couponId = 1;
        long userId = 1;

        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false를 반환한다")
    void availableUserIssueQuantity_2() {
        long couponId = 1;
        long userId = 1;

        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        Assertions.assertFalse(result);
    }
}