package junseok.snr.couponapi.controller;

import junseok.snr.couponapi.controller.dto.CouponIssueRequestDto;
import junseok.snr.couponapi.controller.dto.CouponIssueResponseDto;
import junseok.snr.couponapi.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {
    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto requestDto) {
        couponIssueRequestService.issueRequestWithLockV1(requestDto);
        return new CouponIssueResponseDto(true, null);
    }

}
