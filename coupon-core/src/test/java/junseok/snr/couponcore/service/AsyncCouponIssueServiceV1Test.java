package junseok.snr.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junseok.snr.couponcore.TestConfig;
import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import junseok.snr.couponcore.model.Coupon;
import junseok.snr.couponcore.model.CouponType;
import junseok.snr.couponcore.repository.mysql.CouponJpaRepository;
import junseok.snr.couponcore.repository.redis.dto.CouponIssueRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

class AsyncCouponIssueServiceV1Test extends TestConfig {

    @Autowired
    private AsyncCouponIssueServiceV1 sut;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        final Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예왜를 반환한다")
    void issue_1() {
        long couponId = 1;
        long userId = 1;

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });

        Assertions.assertEquals(ErrorCode.COUPON_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issue_2() {
        long userId = 1_000;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        final Coupon savedCoupon = couponJpaRepository.save(coupon);

        final Long couponId = savedCoupon.getId();

        IntStream.range(0, coupon.getTotalQuantity())
                        .forEach(id -> {
                            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(id));
                        });



        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });

        Assertions.assertEquals(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issue_3() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        final Coupon savedCoupon = couponJpaRepository.save(coupon);

        final Long couponId = savedCoupon.getId();
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));


        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });

        Assertions.assertEquals(ErrorCode.DUPLICATED_COUPON_ISSUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issue_4() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        final Coupon savedCoupon = couponJpaRepository.save(coupon);
        final Long couponId = savedCoupon.getId();

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });

        Assertions.assertEquals(ErrorCode.INVALID_COUPON_ISSUE_DATE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issue_5() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        final Coupon savedCoupon = couponJpaRepository.save(coupon);
        final Long couponId = savedCoupon.getId();

        sut.issue(couponId, userId);

        final boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(couponId), String.valueOf(userId));

        Assertions.assertTrue(isSaved);
    }


    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급요청이 성공하면 쿠폰 발급 큐에 적재된다.")
    void issue_6() throws JsonProcessingException {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        final Coupon savedCoupon = couponJpaRepository.save(coupon);
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(coupon.getId(), userId);
        final Long couponId = savedCoupon.getId();

        sut.issue(couponId, userId);

        final String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());
        final ObjectMapper objectMapper = new ObjectMapper();

        Assertions.assertEquals(objectMapper.writeValueAsString(couponIssueRequest), savedIssueRequest);
    }

}