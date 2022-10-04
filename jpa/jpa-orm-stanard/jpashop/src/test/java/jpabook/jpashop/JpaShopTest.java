package jpabook.jpashop;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaShopTest {

    @Test
    void itemAddTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");

            // 실전에서 상속관계를 통해 얻을수 있는것을 생각해야 한다.
            // 경우에 따라 상속관계 - 조인관계로 설정한다.

            em.persist(book);

            // @Inheritance
            // JPQL 다형성을 활용할수있다. DTYPE을 통해서 조회가 이루어진다.
            List<Item> resultList = em.createQuery("select i from Item as i where type(i) = Book", Item.class)
                    .getResultList();

            for (Item item : resultList) {
                System.out.println("item.getName() = " + item.getName());
            }


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }

    }

}
