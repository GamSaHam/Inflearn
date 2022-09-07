package jpql;

import jpql.domain.Member;
import jpql.domain.Team;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

public class BasicTest {
    @Test
    void basicGrammarTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("user1");
            member.setAge(9);
            em.persist(member);


            // select m from Member as m where m.age > 18
            // JPQL 문법
            // 엔티티와 속성은 대소문자 구분
            // JPQL 키워드는 대소문자 구분 X
            // 엔티티 이름 사용, 테이블 이름이 아님
            // 별칭은 필수 (as 생략가능)

            // 집합과 정렬
            // COUNT, SUM, AVG ...
            // GROUP BY, HAVING

            // TypeQuery, Query
            TypedQuery<Member> typedQuery = em.createQuery("select m from Member as m", Member.class);
            Query query = em.createQuery("select m.username, m.age from Member as m");

            // 여러개
            List<Member> resultList = typedQuery.getResultList();

            for (Member findMember : resultList) {
                System.out.println("findMember.getUsername() = " + findMember.getUsername());
            }

            // 한개
            // 결과없없으면 NoResultException, 둘 이상이면 NonUniqueResultException 에러발생
            typedQuery = em.createQuery("select m from Member as m where m.age < 10", Member.class);
            Member findMember = typedQuery.getSingleResult();
            System.out.println("findMember.getUsername() = " + findMember.getUsername());

            // Spring Data JPA 에서 null or Optional 로 해준다. try{} catch 잡아서 따로 처리 해준다. 

            findMember = em.createQuery("select m from Member as m where m.age < :age", Member.class)
                    .setParameter("age", 10)
                    .getSingleResult();
            System.out.println("findMember.getUsername() = " + findMember.getUsername());

            // 위치 기반도 사용가능하다. 하지만 사용하지 않는 것을 권장

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

}
