package hellopja;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class RelationMappingTest {

    @Test
    void manyToOne() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("member1");
            member.setTeam(team);

            em.persist(member);

//            em.flush();
//            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();

            System.out.println("findTeam.name = " + findTeam.getName());
            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void oneToMany() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team1 = new Team("TeamA");
            em.persist(team1);

            Member member1 = new Member( "HelloA");
            member1.setTeam(team1);

            Member member2 = new Member( "HelloB");
            member2.setTeam(team1);

            em.persist(member1);
            em.persist(member2);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member1.getId());
            List<Member> findMembers = findMember.getTeam().getMembers();

            for (Member member : findMembers) {
                System.out.println("member.id = " + member.getId());
                System.out.println("member.name = " + member.getUserName());
            }

            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void mappedBy() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team1 = new Team("TeamA");
            em.persist(team1);

            Member member1 = new Member( "HelloA");
//            member1.changeTeam(team1); // **
            em.persist(member1);
            // members 항목은 mappedBy로 사용되어진것이라 읽기 전용으로 사용되어진다.
//            team1.getMembers().add(member1); // **

            // 연관관계 편의 메소드는 2개가 있을 경우 문제가 생긴다. (무한루프, ...)
            // 연관관계 편의 메소드는 어디에서 정하는건 하나를 설정하면 된다.
            team1.addMember(member1);

//            em.flush();
//            em.clear();

            // 아래 구문을 보면 em.flush em.clear를 호출을 안할시에 아래
            // 구문에서 members 항목에 size는 0이다. 그러므로 ---1 항목을
            // 추가를 해주어야하고 이게 객체지향 스러운 코드가 된다.

            // 양뱡향 매핑시 양쪽에 데이트를 모두 추가해 주어야 하는게 맞다.

            Team findTeam = em.find(Team.class, team1.getId()); // 1차 캐시
            List<Member> members = findTeam.getMembers();

            System.out.println("===================");
            for (Member member : members) {
                System.out.println("member.getUserName() = " + member.getUserName());
            }
            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void infiniteLoop() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team1 = new Team("TeamA");
            em.persist(team1);

            Member member1 = new Member( "HelloA");
            em.persist(member1);

            // Lombok 함수에 toString 함수는 Member에 team을 호출하게 되고
            // Team 에서는 member를 호출하게 되어서 무한루프가 발생함
            // JSON 생성 라이브러리에서 에러가 발생
            // Lombok toString 쓰지마라
            // Controller 에서 entity를 반환하지 마라
            // DTO를 통해서 반환하라.
            team1.addMember(member1);

            System.out.println("===================");


            // 단반향 매핑만으로도 이미 연관관계 매핑은 완료
            // 양방향 매핑은 반대 방향으로 조회기능이 추가된것뿐
            // JPQL에서 역방향으로 탐색할 일이 많음
            // 단방향 매핑을 잘 하고 양방향매핑을 추가 하면 됨

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
