package junseok.snr.couponcore.repository.mysql;

import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

}
