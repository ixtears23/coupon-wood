package junseok.snr.couponcore.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CouponIssueException extends RuntimeException {
    @Getter
    private final ErrorCode errorCode;
    private final String message;

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode, message);
    }
}
