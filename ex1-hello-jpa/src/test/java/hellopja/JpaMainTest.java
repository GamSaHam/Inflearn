package hellopja;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JpaMainTest {
    @Test
    void crudMemberTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 회원등록
            Member member = new Member();
            member.setId(2L);
            member.setName("김길동");
            em.persist(member);

            // 회원 조회
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            // 회원 수정
            findMember.setName("HelloJPA"); // commit 하기 직전에 update 쿼리 발생


            // dialect 항목을 바꾸어도 sql을 만들어짐
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(10)
                    .getResultList();

            for (Member tempMember : result) {
                System.out.println("tempMember.name = " + tempMember.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void persistentContextTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 비영속
            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJpa");
//
            // 영속
//            System.out.println("=== BEFORE ===");
//            // 1차 캐시저장
//            em.persist(member);
//            System.out.println("=== AFTER ===");
//
//            Member findMember = em.find(Member.class, 101L);
//
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());


            // 준영속
//            em.detach(member);

            // 삭제
//            em.remove(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void cacheTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1차 캐시 - 같은 트랜젝션에서만
            // 동일성 보장
            // 트랜잭션을 지원하는 쓰기 지원
            // 변경 감지(Dirty Checking)
            // 지연 로딩(Lazy Loading)

            // 1차 캐시에서 조회
            // 영속성 컨텐스트에서 조회하면 member1이 있으면
            // member1에 영속성 넣고 캐시된것을 가져온다.
            // member2를 조회를 하면 영속성 컨텐스트에 있는것을 추가한다.

//            Member findMember1 = em.find(Member.class, 101L);
//            // 두번째 조회에서는 영속성 컨텐스트안에 있는 1차 캐시에서 가져온다.
//            Member findMember2 = em.find(Member.class, 101L);
//
//            // 1차 캐시가있어서 가능하다. // Repeatable read 등급에 데이터베이스가 아닌 애플리케이션 차원에서 제공
//            System.out.println("result = "+ (findMember1 == findMember2));

//            // 버퍼링을 모아서 write 할수있다.
//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//
//            em.persist(member1);
//            em.persist(member2);
//

            // 엔티티 수정 변경 감지, Dirty Checking
//            Member member = em.find(Member.class, 150L);
//            member.setName("AA");
            // 1차 캐시에서 memberA 아이디 Entity랑 스냅샷을 비교한다. 다르면 update를 한다.

            // flush 발생
            //            System.out.println("================================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


    @Test
    void flushTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 영속성 컨테스트를 플러시 하는 방법
            // em.flush()
            // tx.commit()
            // JPQL 쿼리 실행

            // 플러시 모드 옵션
            // em.setFlushMode(FlushModeType.AUTO) // 커밋이나 쿼리를 실행할때 (기본값)
            // em.setFlushMode(FlushModeType.COMMIT) // 커밋 할때만 플러쉬

            // 영속성 컨텍스트를 비우지 않음
            // 데이터 베이스랑 동기화
            // 트랜잭션이라는 작업 단위가 중요

            Member member = new Member(200L, "member 200");
            em.persist(member);

            em.flush();

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
    void detachedTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 준영속성 상태로 만드는 방법
            // em.detach(entity)
            // em.clear() // 영속성 컨테이너에서 비운다.
            // em.close() // 영속성 컨텐스트 종료

            Member member = em.find(Member.class, 150L);
            member.setName("AAA");
            em.detach(member);

            // 영속성 컨텐스트에서 1차 캐시 항목에 없앤다.

            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

}