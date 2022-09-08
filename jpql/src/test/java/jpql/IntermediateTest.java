package jpql;

import jpql.domain.*;
import jpql.dto.MemberDTO;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

public class IntermediateTest {

    @Test
    void intermediateTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("a");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            // 연관관계 편의 메서드
            member.changeTeam(team);
            em.persist(member);

            // select m.username -> 상태 필드
            // from Member m
            // join m.team t    -> 단일 값 연관 필드
            // join m.orers o   -> 커렉션 값 연관 필드
            // where t.name = '팀A'

            // 상태필드(state field) 단순 값을 저장하기 위한 필드
            // 연관필드(association field) 연관관계를 위한 필드

            // 경로 표현식 특징
            // 상태 필드: 경로 탐색의 끝, 탐색X

            em.createQuery("select m.username from Member m").getResultList(); // m.username.을 접근할 수 없다.
            em.createQuery("select m.team from Member m").getResultList(); // 묵시적 내부 조인 발생, 탐색O (조심해서 사용해야 한다)
            //컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X
            em.createQuery("select t.members from Team t").getResultList();
            em.createQuery("select m.username from Team t join t.members m").getResultList(); // 명시적 조인을 한다.

            // 묵시적 조인을 쓰면 SQL 튜닝하기 힘들다.

            // select m.team from Member m

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
