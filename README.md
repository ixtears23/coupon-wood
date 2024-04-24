# 쿠폰 발급 시스템
## 개발 환경
java21, gradle(multi module), jpa, querydsl,  
mysql, h2, redis, docker, locust, sqs  

## 아키텍처
- DB 성능 개선 <- Redis 캐시
- Redis 성능 개선 <- Caffein Cache(로컬 캐시)
- Application 성능 개선 <- SQS 및 서버 분리
- 모니터링(Prometheus, Grafana)
- 성능측정([locust](https://locust.io/))
![쿠폰발급](https://github.com/ixtears23/coupon-wood/blob/master/Coupon%20%E1%84%87%E1%85%A1%E1%86%AF%E1%84%80%E1%85%B3%E1%86%B8.svg)
