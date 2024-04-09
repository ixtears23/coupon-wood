package junseok.snr.couponcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아 있다면 true를 반환한다")
    void test() {
        final Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        final boolean result = coupon.availableIssueQuantity();

        assertThat(result).isTrue();
    }


}