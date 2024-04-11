package junseok.snr.couponcore.service;

import junseok.snr.couponcore.TestConfig;
import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import junseok.snr.couponcore.model.CouponIssue;
import junseok.snr.couponcore.repository.mysql.CouponIssueJpaRepository;
import junseok.snr.couponcore.repository.mysql.CouponIssueRepository;
import junseok.snr.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    private CouponIssueService couponIssueService;
    @Autowired
    private CouponIssueRepository couponIssueRepository;
    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;
    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @BeforeEach
    public void clean() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환한다")
    @Test
    void saveCouponIssue01() {

        final CouponIssue couponIssue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();

        couponIssueJpaRepository.save(couponIssue);


        final CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId());
        });

        assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
    }

    @DisplayName("쿠폰 발급 내역이 존재하지 않는다면 쿠폰을 발급한다")
    @Test
    void saveCouponIssue02() {
        final long couponId = 1L;
        final long userId = 1L;

        final CouponIssue savedCouponIssue = couponIssueService.saveCouponIssue(couponId, userId);

        assertTrue(couponIssueJpaRepository.findById(savedCouponIssue.getId()).isPresent());
    }

}