package com.jpasjop.service;

import com.jpasjop.domain.Delivery;
import com.jpasjop.domain.Member;
import com.jpasjop.domain.Order;
import com.jpasjop.domain.OrderItem;
import com.jpasjop.domain.item.Item;
import com.jpasjop.repository.ItemRepository;
import com.jpasjop.repository.MemberRepository;
import com.jpasjop.repository.OrderRepository;
import com.jpasjop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * @param memberId
     * @param itemId
     * @param count
     * @return
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        // Cascade 로 인해서 delivery 항목하고 orderItem 항목을 persist안해도 된다.
        // 라이프 사이클이 동일하게 관리를할때 delivery 항목을 참조를 Order만 할때

        return order.getId();
    }


    /**
     * 주문 취소
     * @param orderId
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel(); // 도메인 모델 패턴
        // 트렌젝션 스크립트 패턴을 사용하지 않는다. JPA로 인한
        // 무엇이 유지보수하기 쉬운가에 따라 판별하게 되어 있다.
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }

}
