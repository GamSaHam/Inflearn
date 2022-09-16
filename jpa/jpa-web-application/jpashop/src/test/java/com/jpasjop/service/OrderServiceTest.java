package com.jpasjop.service;

import com.jpasjop.domain.Address;
import com.jpasjop.domain.Member;
import com.jpasjop.domain.Order;
import com.jpasjop.domain.OrderStatus;
import com.jpasjop.domain.item.Book;
import com.jpasjop.exception.NotEnoughStockException;
import com.jpasjop.repository.OrderRepository;
import com.jpasjop.repository.OrderSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void doOrder() throws Exception {

        // 단위테스트가 중요하다. == 함수 테스트
        // given
        Member member = createMember("회원A");
        Book book = createBook();

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        // 상품 주문시 상태는 Order
        assertThat(OrderStatus.ORDER).isEqualTo(findOrder.getStatus());
        // 주문한 상품 종류 수가 정확해야 한다.
        assertThat(1).isEqualTo(findOrder.getOrderItems().size());
        // 주문 가격은 가격 * 수량이다.
        assertThat(book.getPrice() * orderCount).isEqualTo(findOrder.getTotalPrice());
        // 주문 수량만큼 재고가 줄어야 한다.
        assertThat(8).isEqualTo(book.getStockQuantity());
    }

    @Test
    public void exceedInventory() throws Exception {
        // given
        Member member = createMember("회원A");
        Book book = createBook();

        // when
        int orderCount = 11;

        //then
        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount)).isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    public void cancelOrder() throws Exception {
        // given
        Member member = createMember("회원A");
        Book book = createBook();

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);
        // 주문 취소시 상태는 CANCEL 이다.
        assertThat(OrderStatus.CANCEL).isEqualTo(order.getStatus());
        // 주문이 취소된 상품은 그만큼 재고가 증가해야 한다.
        assertThat(10).isEqualTo(book.getStockQuantity());


        // 도메인 모델 패턴에서는 도메인상에서 비지니스 로직이 돌아가기때문에 해당하는것을 유닛테스트를 진행 할 수 있다.

    }

    @Test
    public void findOrders() throws Exception {
        // given
        Member member = createMember("회원A");
        Member member2 = createMember("회원B");
        Book book = createBook();

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), 1);
        Long orderId2 = orderService.order(member2.getId(), book.getId(), 2);

        List<Order> findOrders = orderService.findOrders(new OrderSearch("회원A", OrderStatus.ORDER));
        //then
        assertThat(findOrders.size()).isEqualTo(1);
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA 책");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "관악구", "685-175"));
        em.persist(member);
        return member;
    }

}