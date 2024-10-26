package coupon.coupon.service;

import coupon.coupon.domain.MemberCoupon;
import coupon.coupon.dto.MemberCouponResponse;
import coupon.coupon.repository.MemberCouponRepository;
import coupon.datasource.aop.WriteTransaction;
import coupon.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;

    @WriteTransaction
    @Transactional
    public MemberCoupon issueMemberCoupon(MemberCoupon memberCoupon) {
        validateIssuedMemberCouponCount(memberCoupon);
        return memberCouponRepository.save(memberCoupon);
    }

    private void validateIssuedMemberCouponCount(MemberCoupon memberCoupon) {
        List<MemberCoupon> memberCoupons = memberCouponRepository.findAllByCouponAndMember(
                memberCoupon.getCoupon(),
                memberCoupon.getMember()
        );

        if (memberCoupons.size() >= 5) {
            throw new IllegalArgumentException("이미 5개 이상 발급된 쿠폰입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<MemberCouponResponse> readMemberCoupons(Member member) {
        return memberCouponRepository.findAllByMember(member).stream()
                .map(memberCoupon -> MemberCouponResponse.from(
                                memberCoupon,
                                couponService.readCoupon(memberCoupon.getCoupon().getId())
                        )
                ).toList();
    }
}
