package hellopja;

import org.hibernate.Hibernate;
import org.hibernate.jpa.internal.PersistenceUnitUtilImpl;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

public class ProxyTest {
    @Test
    void proxyTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("팀1");

            em.persist(team);

            Member member = new Member();
            member.setUserName("홍길동");
            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());

            printMember(findMember);
//            printMemberAndTeam(findMember);


            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    private void printMember(Member member) {
        String userName = member.getUserName();
        System.out.println("userName = " + userName);
    }

    private void printMemberAndTeam(Member member) {
        String userName = member.getUserName();
        System.out.println("userName = " + userName);

        Team team = member.getTeam();
        System.out.println("team.getName() = " + team.getName());
    }

    @Test
    void proxyTest2() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member1 = new Member();
            member1.setUserName("hello");

            em.persist(member1);


            Member member2 = new Member();
            member2.setUserName("hello");

            em.persist(member2);

            em.flush();
            em.clear();

//            Member findMember = em.find(Member.class, member.getId());
//            System.out.println("findMember.getUserName() = " + findMember.getUserName());

            Member findMember = em.getReference(Member.class, member1.getId());
            System.out.println("findMember.getClass() = " + findMember.getClass());
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getUsername() = " + findMember.getUserName());
            System.out.println("findMember.getUsername() = " + findMember.getUserName());
            System.out.println("===================");

            //가짜(프록시) 객체를 전달한다.
            // 하이버네이트에서
            // getName 항목이 없으면 영속성 컨텐스트를 요청하고
            // MemberProxy 항목에 target 항목에 있는 Member 객체에 getName 을 호출한다.

            // 프록시의 특징
            // 프록시 객체는 처음 사용할 때 한번만 초가화
            // 조회 된다고 해서 교체된다고 하지 않는다.
            // 타입 체크시 주의해야함 == 비교 대신 instance of 사용

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.getReference(Member.class, member2.getId());

            logic(m1, m2);

            // 영속성 컨텍스트에서 찾는 엔티티가 이미 있으면 영속성 컨텐스트에서 가지고 온다.


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    private void logic(Member m1, Member m2) {
        System.out.println("m1 == m2:" + (m1.getClass() == m2.getClass())); // false
        System.out.println("m1 instanceof Member:" + (m1 instanceof Member));
        System.out.println("m2 instanceof Member:" + (m2 instanceof Member));
    }


    @Test
    void proxyTest3() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUserName("hello");

            em.persist(member);

            em.flush();
            em.clear();

            // 영속성 컨텍스트에서 찾는 엔티티가 이미 있으면 영속성 컨텐스트에서 가지고 온다.
            Member findMember = em.find(Member.class, member.getId());
            System.out.println("findMember.getClass() = " + findMember.getClass());

            Member reference = em.getReference(Member.class, member.getId());
            System.out.println("reference.getClass() = " + reference.getClass());

            // 트렌잭션당 연속성 컨텐스트 하나
            System.out.println("a == a" + (findMember == reference));

            // 프록시로 조회가 되면
            // em.find 항목에서 프록시를 가지고 온다.


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void proxyTest4() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUserName("hello");

            em.persist(member);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member.getId());
            System.out.println("findMember.getClass() = " + refMember.getClass());

            // 영속성 컨텐스트에서 관리안한다고 선언
//            em.detach(refMember);
//            em.clear();
//            em.close();

            System.out.println("emf.getPersistenceUnitUtil().isLoaded(refMember) = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
//            System.out.println("refMember.getUserName() = " + refMember.getUserName()); //강제초기화

            Hibernate.initialize(refMember); // 강제초기화 // JPA 표준에서는 강제 초기화 항목이 없음

            // getReference 실무에서 사용하지 않지만 즉시 로딩과 지연로딩을 이해하기 위해 배운내용
            System.out.println("emf.getPersistenceUnitUtil().isLoaded(refMember) = " + emf.getPersistenceUnitUtil().isLoaded(refMember));

            tx.commit();
        } catch (Exception e) {
            // could not initialize proxy

            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


    @Test
    void lazyLoadingTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("team1");
            em.persist(team);

            Member member = new Member();
            member.setUserName("hello");
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            // Member 항목에 Team을 Lazy로 설정하여 select 항목에서 Team을 조회를 안하는것을 확인
            // Team 항목을 프록시 형태로 설정이 된다.
            Member findMember = em.find(Member.class, member.getId());

            // 프록시로 나온것을 확인할 수 있다.
            System.out.println("findMember.getTeam().getClass() = " + findMember.getTeam().getClass());

            System.out.println("=============");
            System.out.println("findMember.getTeam().getName() = " + findMember.getTeam().getName()); // 하이버네이트 초기화가 이루어짐
            System.out.println("=============");

            // 실무에서는 즉시로딩 사용하면 안된다.
            // 가급적이면 지연 로딩만 사용
            // 즉시 로딩하면 예상하지 못한 쿼리가 나간다. 조인이 너무 많이 나간다.
            // @ManyToOne, @OneToOne은 기본이 즉시 로딩 Lazy로 설정
            // 즉시 로딩은 JPQL에서 N + 1 문제를 일으킨다.

            tx.commit();
        } catch (Exception e) {
            // could not initialize proxy

            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void lazyLoadingTest2() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("team1");
            em.persist(team);

            Member member = new Member();
            member.setUserName("hello");
            member.setTeam(team);
            em.persist(member);

            Team team2 = new Team();
            team2.setName("team1");
            em.persist(team2);


            Member member2 = new Member();
            member2.setUserName("hello");
            member2.setTeam(team2);
            em.persist(member2);

            em.flush();
            em.clear();

            // 즉시 로딩은 JPQL에서 N + 1 문제를 일으킨다.
            List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
            // select member와 team항목으로 두개 select절이 발생한다.
            // N + 1의 추가 쿼리가 발생한다.

            // 패치 조인 으로 사용을 하면 해결은 할수 있다.
            List<Member> members2 = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();

            // @OneToMany, @ManyToMany 지연로딩이 기본이 되어있다.


            tx.commit();
        } catch (Exception e) {
            // could not initialize proxy

            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void caseCadeTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);


            // 부모 위주로 세팅하고 싶을 경우
            // 현제 child 항목을 영속성 컨테스트에 추가해주지 않아서 insert 항목이 추가되지 않는다.
            // 연관관계를 매핑하는 것과 아무 관련이 없다 cascade 항목은
            // 엔티티를 영속화 할때 연관된 엔티티도 함께 영속화 하는 편리함을 제공한다.
            // ALL: 모두 적용
            // PERSIS: 영속
            // REMOVE: 삭제

            // Child 가 소유자가 하나일때 다른 연관관계가 없을때만 사용가능
            // Parent 라이프 사이클이 똑같을 때
            // 단일 소유자 일때

            // 고아 객체: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제

            // 참조하는 곳이 하나일때만 사용
            // 특정 엔티티가 개인 소유할때만 사용
            // CaseCade.REMOVE 처럼 동작한다.

            // CaseCade.ALL orphanRemoval = true
            // 두옵션을 활성하면 생명주기를 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있음
            // 도메인 주도 설계 Aggregate Root개념을 구현 할때 유용


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
