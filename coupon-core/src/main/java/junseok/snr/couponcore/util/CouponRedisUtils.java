package junseok.snr.couponcore.util;

public class CouponRedisUtils {
    public static String getIssueRequestKey(long couponId) {
        return "issue.request.couponId=%s".formatted(couponId);
    }
}
