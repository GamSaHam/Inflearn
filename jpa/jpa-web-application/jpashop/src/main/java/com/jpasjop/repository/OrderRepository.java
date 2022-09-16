package com.jpasjop.repository;

import com.jpasjop.domain.Order;
import com.jpasjop.domain.OrderStatus;
import com.jpasjop.domain.QMember;
import com.jpasjop.domain.QOrder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jpasjop.domain.QMember.member;
import static com.jpasjop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {

        JPAQueryFactory query = new JPAQueryFactory(em); // (1)


        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus())
                    , nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression statusEq(OrderStatus statusCond) {
        if(statusCond == null) {
            return null;
        }

        return order.status.eq(statusCond);
    }

    private BooleanExpression nameLike(String nameCond) {

        if(StringUtils.hasText(nameCond) == false) {
            return null;
        }

        return member.name.like(nameCond);
    }


}
