package junseok.snr.couponapi.service;

import junseok.snr.couponapi.controller.dto.CouponIssueRequestDto;
import junseok.snr.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService  {
    private final CouponIssueService couponIssueService;

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
         log.info("쿠폰 발급 완료. couponId: {}, userId:{}", requestDto.couponId(), requestDto.userId());
    }
}
