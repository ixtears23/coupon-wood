package junseok.snr.couponapi.controller;

import junseok.snr.couponapi.controller.dto.CouponIssueResponseDto;
import junseok.snr.couponcore.exception.CouponIssueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponControllerAdvice {

    @ExceptionHandler
    public CouponIssueResponseDto handle(CouponIssueException couponIssueException) {
        return new CouponIssueResponseDto(false, couponIssueException.getErrorCode().getMessage());
    }
}
