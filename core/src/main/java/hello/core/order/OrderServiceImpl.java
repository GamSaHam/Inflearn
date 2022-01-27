package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // final 로 정의된 생성자를 자동 만들어준다.
public class OrderServiceImpl implements OrderService {

    // @Autowired 필드 주입 : 필드 주입은 외부에서 테스트 코드로 변경 할 수 없기 때문에 권장하지 않는다.
    // 테스트 코드나 Config 파일 에서는 사용하는 경우도 있다.
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // 생성자가 하나이면 @Autowired 생략해도 된다.
    // final 키워드를 추가하게 하므로 컴파일 오류를 발생시킨다.
//    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//
//        System.out.println("memberRepository = " + memberRepository);
//        System.out.println("discountPolicy = " + discountPolicy);
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

//    @Autowired // 수정자 의존성 주입
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired // 일반 메서드 의존성 주입 - 생성자 주입, 수정자 주입을 하기 때문에 사용되지 않는다.
//    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy){
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

    // @Autowired 는 스프링 컨테이너에서 동작 한다.

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {

        Member member = memberRepository.findById(memberId);
        // 단일 책임 원칙을 잘지킨 케이스
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
