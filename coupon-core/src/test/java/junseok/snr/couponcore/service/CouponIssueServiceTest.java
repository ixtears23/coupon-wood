package junseok.snr.couponcore.service;

import junseok.snr.couponcore.TestConfig;
import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import junseok.snr.couponcore.model.Coupon;
import junseok.snr.couponcore.model.CouponIssue;
import junseok.snr.couponcore.model.CouponType;
import junseok.snr.couponcore.repository.mysql.CouponIssueJpaRepository;
import junseok.snr.couponcore.repository.mysql.CouponIssueRepository;
import junseok.snr.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

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

    @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다")
    @Test
    void issue01() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        couponIssueService.issue(coupon.getId(), userId);

        final Coupon savedCoupon = couponJpaRepository.findById(coupon.getId()).get();

        assertEquals(savedCoupon.getIssuedQuantity(), 1);

        final CouponIssue firstCouponIssue = couponIssueRepository.findFirstCouponIssue(savedCoupon.getId(), userId);
        assertNotNull(firstCouponIssue);
    }


    @DisplayName("발급 수량에 문제가 있다면 예외를 반환한다")
    @Test
    void issue02() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        final CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }

    @DisplayName("발급 기한에 문제가 있다면 예외를 반환한다")
    @Test
    void issue03() {
        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(-1))
                .build();

        couponJpaRepository.save(coupon);

        final CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

    @DisplayName("중복 발급 검증에 문제가 있다면 예외를 반환한다")
    @Test
    void issueTest04() {

        long userId = 1;

        final Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        final CouponIssue couponIssue = CouponIssue.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();

        couponIssueJpaRepository.save(couponIssue);

        final CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
    }


    @DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다")
    @Test
    void issueTest05() {

        long userId = 1;
        long couponId = 1;

        final CouponIssueException exception = assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(couponId, userId);
        });

        assertEquals(exception.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
    }


}