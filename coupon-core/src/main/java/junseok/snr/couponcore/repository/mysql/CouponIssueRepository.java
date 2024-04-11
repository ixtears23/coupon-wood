package junseok.snr.couponcore.repository.mysql;

import com.querydsl.jpa.JPQLQueryFactory;
import junseok.snr.couponcore.model.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static junseok.snr.couponcore.model.QCouponIssue.couponIssue;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    public CouponIssue findFirstCouponIssue(long couponId, long userId) {
        return jpqlQueryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId))
                .where((couponIssue.userId.eq(userId)))
                .fetchFirst();

    }
}
