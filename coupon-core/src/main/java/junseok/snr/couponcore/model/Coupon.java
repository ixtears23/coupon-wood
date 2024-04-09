package junseok.snr.couponcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Coupon extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    public boolean availableIssueQuantity() {
        if (totalQuantity == null) return true;

        return totalQuantity > issuedQuantity;
    }

    public boolean availableIssueDate() {
        final LocalDateTime now = LocalDateTime.now();

        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }



    public void issue() {
        if (!availableIssueQuantity()) {
            throw new RuntimeException("수량 검증");
        }

        if (!availableIssueDate()) {
            throw new RuntimeException("기한 검증");
        }

        issuedQuantity++;
    }

}
