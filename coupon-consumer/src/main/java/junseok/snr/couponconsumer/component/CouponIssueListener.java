package junseok.snr.couponconsumer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junseok.snr.couponcore.repository.redis.RedisRepository;
import junseok.snr.couponcore.repository.redis.dto.CouponIssueRequest;
import junseok.snr.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static junseok.snr.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class CouponIssueListener {
    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;
    private final String issueRequestQueueKey = getIssueRequestQueueKey();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 쿠폰 발급 대기열 큐를 읽어서 적재
    @Scheduled(fixedDelay = 10_000L)
    public void issue() throws JsonProcessingException {

        log.info("listen...");
        while(existCouponIssueTarget()) {
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작 target: %s".formatted(target));
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 target: %s".formatted(target));

            removeIssuedTarget();
        }
    }

    private void removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey);
    }

    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {
        return objectMapper.readValue(redisRepository.lIndex(issueRequestQueueKey, 0), CouponIssueRequest.class);
    }

    private boolean existCouponIssueTarget() {
        return redisRepository.lSize(issueRequestQueueKey) > 0;
    }

}
