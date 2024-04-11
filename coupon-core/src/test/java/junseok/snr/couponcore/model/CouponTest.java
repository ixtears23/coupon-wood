package junseok.snr.couponcore.model;

import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아 있다면 true를 반환한다")
    void availableIssueQuantityTest01() {
        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        final boolean result = coupon.availableIssueQuantity();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("발급 수량이 소진 되었다면 false를 반환한다")
    void availableIssueQuantityTest02() {
        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        final boolean result = coupon.availableIssueQuantity();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("최대 발급 수량이 설정되지 않았다면 True를 반환한다")
    void availableIssueQuantityTest03() {
        final Coupon coupon = Coupon.builder()
                .build();

        final boolean result = coupon.availableIssueQuantity();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("발급 기간이 시작되지 않았다면 false를 반환한다")
    void availableIssueDateTest01() {
        final Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        final boolean result = coupon.availableIssueDate();

        assertThat(result).isFalse() ;
    }

    @Test
    @DisplayName("발급 기간에 해당되면 true를 반환한다")
    void availableIssueDateTest02() {
        final Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        final boolean result = coupon.availableIssueDate();

        assertThat(result).isTrue() ;
    }

    @Test
    @DisplayName("발급 기간이 종료되면 false를 반환한다")
    void availableIssueDateTest03() {
        final Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        final boolean result = coupon.availableIssueDate();

        assertThat(result).isFalse() ;
    }

    @Test
    @DisplayName("발급 수량과 발급기간이 유효하다면 발급에 성공한다")
    void issueTest01() {

        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        coupon.issue();

        assertThat(coupon.getIssuedQuantity()).isEqualTo(100);

    }

    @Test
    @DisplayName("발급 수량을 초과하면 예외를 반환한다")
    void issueTest02() {

        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class);

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .extracting(exception -> ((CouponIssueException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("발급 기간이 아니면 예외를 반환한다")
    void issueTest03() {

        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .extracting(exception -> ((CouponIssueException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

}