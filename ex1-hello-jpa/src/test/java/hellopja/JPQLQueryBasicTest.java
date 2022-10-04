package hellopja;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPQLQueryBasicTest {
    @Test
    void JPQLBasicGrammarTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            em.persist(member);

            // JPQL은 객체지향 쿼리이다.
//            List<Member> findMembers = em.createQuery(
//                    "select m from Member as m where m.userName like '%kim%'", Member.class
//            ).getResultList();
//
//            for (Member findMember : findMembers) {
//                System.out.println("findMember.getUserName() = " + findMember.getUserName());
//            }
//
            // JPQL은 동적쿼리를 만들어 내기가 어렵다.

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);

            // 쿼리 생성
            // 자바 코드라 컴파일 에러가 나고 동적 코드를 짜기가 쉽다.
            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));

            List<Member> findMembers = em.createQuery(cq).getResultList();
            for (Member findMember : findMembers) {
                System.out.println("findMember.getUserName() = " + findMember.getUsername());
            }

            // 실무에서는 유지보수하기가 힘든다.
            // 저자가 망한 스펙이라고 정의한다.
            // 너무 복잡하고 실용성이 없다.
            // Criteria 대신 QueryDSL 사용 권장

            // flush -> commit, query 일때

            // NativeQuery
            List<Member> resultList = em.createNativeQuery(
                    "select MEMBER_ID, city, street, zipcode, USERNAME from MEMBER", Member.class
            ).getResultList();

            for (Member findMember : resultList) {
                System.out.println("findMember.getUsername() = " + findMember.getUsername());
            }

            // 저자는 Native Query 보다 SpringJdbcTemplate 항목을 사용한다.

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void JPQLBasicGrammarTest2() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 엔티티 객체를 대상으로 쿼리한다.
            // sql 추상화 해서 특정데이터베이스 SQL에 의존하지 않는다.
            // JPQL은 결국 SQL로 변환한다.


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
