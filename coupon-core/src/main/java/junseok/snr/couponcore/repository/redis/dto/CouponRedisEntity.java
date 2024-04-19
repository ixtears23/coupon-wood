package junseok.snr.couponcore.repository.redis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import junseok.snr.couponcore.exception.CouponIssueException;
import junseok.snr.couponcore.exception.ErrorCode;
import junseok.snr.couponcore.model.Coupon;
import junseok.snr.couponcore.model.CouponType;

import java.time.LocalDateTime;

public record CouponRedisEntity(
    Long id,
    CouponType couponType,
    Integer totalQuantity,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateIssueStart,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateIssueEnd
) {
    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd()
        );
    }

    private boolean availableIssueDate() {
        final LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public void checkIssuableCoupon() {
        if (!availableIssueDate()) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다. couponId : %s, issueStart : %s, issueEnd : %s".formatted(id, dateIssueStart, dateIssueEnd));
        }
    }
}
