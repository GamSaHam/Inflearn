package com.jpasjop.service;

import com.jpasjop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
public class ItemUpdateTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // given
        Book book = em.find(Book.class, 1L);

        // when
        book.setName("asdfgt");
        // 아이템이 변경되면 Update 문이 나간다.
        // 트렌젝션 커밋 시점에서
        // 준영속 컨텐스트 - 영속성 컨텐스트에서 관리안하는 객체
        // - getId가 있는것을 말한다.
        // JPA 식별할수 있는 데이터를 가지고 있다.

        // Book book = new Book();
        // book.setId(form.getId());
        // book.setName("변경");

        // 변경 감지 기능 사용 Dirty checking
        // 병합 사용
        // updateItem 항목과 비슷하다고 볼수 있다.

        // merge 를 실행
        // 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
        // 영속성컨텐스트에 올라간 엔티티를 반환해 준다.
        // Item merge = em.merge(item) // item 에는 영속성 컨텐스트에서 영속상태가 된것이 아니라 merge 항목이 영속 상태가 된다.
        // 병합시 null 이 들어가 있으면 null 로 업데이트를 해버린다.
        // merge 를 쓰면 안되고 변경 감지를 사용해야 한다.

        //then
    }

}
