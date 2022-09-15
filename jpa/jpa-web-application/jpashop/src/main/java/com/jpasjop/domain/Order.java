package com.jpasjop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Order에 엑세스를 많이 할것이라고 생각을 하고 연관관계 주인을 설정을 했다.
    @OneToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;  // 주문상태

    protected Order() {
    }

    // 연과관계 편의메서드
    public void changeDelivery(Delivery delivery) {
        if(this.delivery != null) {
            this.delivery.setOrder(null);
        }

        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void changeMember(Member member) {
        if(this.member != null) {
            this.member.getOrders().remove(this);
        }

        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    //== 생성 메서드 ==//
    /**
     * 주문 생성
     * @param member
     * @param delivery
     * @param orderItems 가변인자
     * @return
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.changeMember(member);
        order.changeDelivery(delivery);

        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //== 비즈니스 로직 ==//
    /**
     * 주문취소
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


    //== 조회 로직 ==//
    /**
     * 전체 주문 가격 조회
     * @return
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

}



