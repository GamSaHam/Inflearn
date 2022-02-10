package hello.core.order;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceImplTest {

    @Test
    void createOrder() {

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "ItemA", Grade.VIP));

        // 수정자 주입으로 짜면 코드 안에 어떠한 의존관계를 대입해줘야하는지 일일이 알아야한다.
        // 생성자 주입으로 하면 컴파일 오류가 발생한다.
        // Mock 라이브러리도 만들 수 있다.
        OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
        Order order = orderService.createOrder(1L, "ItemA", 10000);
        
        assertThat(order.getDiscountPrice()).isEqualTo(1000);

    }
}
