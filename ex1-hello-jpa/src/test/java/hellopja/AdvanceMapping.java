package hellopja;


import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AdvanceMapping {

    @Test
    void advanceMappingTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 조인 전략  아래 PK, FK로 설정하고 부모에서 DTYPE 설정
            // 단일 테이블 전략 전부 ITEM 항목에 내용이 설정되어진다.
            // 구현 클래스마다 테이블 전략

            // 객체는 똑같고 디비 설계에서 설정을 하면 된다.
            // JPA 기본은 단일 테이블 전략을 따름

            Movie movie = new Movie();
            movie.setActor("A");
            movie.setDirector("B");
            movie.setName("바람과 함께 사라지다.");
            movie.setPrice(10000);

            em.persist(movie);
            
            em.flush();
            em.clear();

            Movie findMovie = em.find(Movie.class, movie.getId());
            System.out.println("findMovie.getName() = " + findMovie.getName());

            // TABLE_PER_CLASS
            // ITEM 항목에 테이블이 만들어지지 않음
            // ITEM 항목에서 abstract항목을 추가해 주어야 한다.
            // 다형성을 할경우 union 쿼리로 실행해서 성능 이슈가 생긴다.
            Item findItem = em.find(Item.class, movie.getId());
            System.out.println("findItem.getName() = " + findItem.getName());

            // 조인 전력
            // 조인전략은 정규화가 되어있다.
            // 외래키 참조 무결성 제약조건을 활용가능하다.
            // 저장공간 효율화

            // 조회시 조인, insert 에서 2번 들어감
            // 조인전략이 기본이라고 생각해도 된다.

            // 단일 테이블 전략
            // 조인이 없음, 조회 쿼리가 단순함

            // 자식 엔티티는 널을 허용해줘야 한다.
            // 모든 데이터를 저장해야하서 테이블이 커질수가 있다.

            // 구현 클래스마다 테이블 전략
            // 사용되어지면 안됨 데이터 베이스 설계자 ORM 전문가 둘다 추천 X
            // 묵어지는 구문이 없다.
            // not null 제약조건 사용가능

            tx.commit();
        } catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


}
