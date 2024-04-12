package junseok.snr.couponapi.service;

import junseok.snr.couponapi.controller.dto.CouponIssueRequestDto;
import junseok.snr.couponcore.component.DistributeLockExecutor;
import junseok.snr.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService  {
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute("lock_" + requestDto.couponId(), 10_000, 10_000, () ->
            couponIssueService.issue(requestDto.couponId(), requestDto.userId())
        );
        log.info("쿠폰 발급 완료. couponId: {}, userId:{}", requestDto.couponId(), requestDto.userId());
    }
}
