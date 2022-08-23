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

            // fk, fk 중간 테이블보다 기본키를 잡는것 보다 중간테이블이라도 pk로 따로 두는게 낮다.
            // 순간에 제약조건이 상 보기 좋은데 운영을 하면서 좋지 않다.

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
    void annotationExam() {


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // ManyToMany 항목은 실무에서 안쓰임
            // OneToOne은 가끔 쓰임

            // OneToMany 단방향 매핑은 실무에서 권장하지 않는다.
            // OneToMany 하면 insert insert update로 쿼리항목이 늘어난다.
            // JoinColumn 을 사용안하면 조인 테이블이 생성된다.

            // @OneToOne은 ManyToOne이랑 사용되어지는 다른게 없다.
            // 요구사항이 변경이 될수도 있으므로 ManyToOne 항목에 있는 것을 먼저 생각을 하고 주인을
            // 설정하면 된다.
            // 저자는 Member쪽에 주인을 두는것으로 선택을 함

            // ManyToMany 에서는 사용하지 않는다.
            // 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야 함.

            // 객체는 다대다 가능한다. ORM에서는 지원을 못한다.

            // 중간테이블에서 주문시간, 수량 같은 항목이 들어갈수도 있다.
            // 이정보를 사용할수 없다.
            // 예측 불가능한 쿼리가 날라감

            // 중간 에 MemberProduct 항목을 만들어줘서 ManyToMany 항목을 사용하지 않는다.
            Member member = new Member();
            member.setUserName("member1");
            em.persist(member);

            Product product = new Product();
            product.setName("product1");
            em.persist(product);

            MemberProduct memberProduct = new MemberProduct();
            memberProduct.setMember(member);
            memberProduct.setProduct(product);
            em.persist(memberProduct);


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
