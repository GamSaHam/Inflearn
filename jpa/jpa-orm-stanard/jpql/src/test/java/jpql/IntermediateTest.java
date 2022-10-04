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



    @Test
    void fetchJoinTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("팀A");
            em.persist(team);

            Team team2 = new Team();
            team2.setName("팀B");
            em.persist(team2);

            Member member = new Member();
            member.setUsername("회원1");
            member.changeTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(team);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(team2);
            em.persist(member3);

            em.flush();
            em.clear();

            System.out.println("===========");
            // 멤버를 조인할껀데 팀항목을 가지고 온다.
            List<Member> resultList = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();

            for (Member findMember : resultList) {
                System.out.print("findMember.getUsername() = " + findMember.getUsername());
                System.out.println(" findMember.getTeam().getName() = " + findMember.getTeam().getName());
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차캐시)
                // 회원3, 팀B(SQL)

                // 회원 100명 -> N + 1
            }

//            List<Team> findTeamList = em.createQuery("select distinct t From Team t join fetch t.members", Team.class).getResultList();
//
//            for (Team findTeam : findTeamList) {
//                System.out.println("findTeam.getName() = " + findTeam.getName() + " members = " + findTeam.getMembers().size());
//
//                for (Member findTeamMember : findTeam.getMembers()) {
//                    System.out.println("-> findTeamMember = " + findTeamMember);
//                }
//            }

            // DISTINCT는 중복된 결과를 제거하는 명령
            // 1. SQL에 DISTINCT를 주가
            // 2. 애플리케이셔에서 엔티티 중복제거

            // 같은 식별자를 가진 Team 엔티티 제거
            // 일대다 는 뻥튀기가 될수가있다. inner 조인 특성상

            // 페치 조인과 일반 조인의 차이
            // 데이터가 포함이 안되어 있다.
            List<Team> findTeamList = em.createQuery("select distinct t From Team t join t.members", Team.class).getResultList();

            for (Team findTeam : findTeamList) {
                System.out.println("findTeam.getName() = " + findTeam.getName() + " members = " + findTeam.getMembers().size());

                for (Member findTeamMember : findTeam.getMembers()) {
                    System.out.println("-> findTeamMember = " + findTeamMember);
                }
            }

            // 페치 조인을 사용할때만 연관된 엔티티도 조회
            // N + 1 의 문제를 대부분 해결함

            // 페치 조인의 특징과 한계
            // 패치조인에는 엘리어스를 줄수없다
            // em.createQuery("select distinct t From Team t join t.members as m", Team.class).getResultList();
            // 둘 이상의 컬렉션은 페치 조인 할 수 없다.
            // 컬렉션을 페치 조인하면 페이지 API를 사용할수 없다.
            // (일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능)
            // @BatchSize(size = 100)
            // 배치사이즈를 설정하면 in 으로 설정되어 N + 1 문제를 해결한다.

            // 연관된 엔티티들을 SQL 한 번으로 조회 - 성능 최적화
            // 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
            // @OneToMany(fetch = FetchType.LAZY) // 글로벌 로딩 전략
            // 실무에서는 글로벌 로딩 전략을 모두 지연 로딩
            // 최적화가 필요한 곳은 페치 조인 적용

            // 정리
            // 모든 것을 페치 조인으로 해결할 수는 없음
            // 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적
            // 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전햐 다른 결과를 내야 하면
            // 페치 조인보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적

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
    void inheritanceTest() {
        // TYPE
        // select i from item i where type(i) in (Book, Movie)
        // TREAT (JPA 2.1)
        // select i from item i where treat(i as Book).author = 'kim' - 다운케스팅처럼 쓸 수 있다.
        // select i.* from item i where i.DTYPE = 'B' and i.auther = 'kim'
    }
    @Test
    void usingEntityDirectlyTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("팀A");
            em.persist(team);

            Team team2 = new Team();
            team2.setName("팀B");
            em.persist(team2);

            Member member = new Member();
            member.setUsername("회원1");
            member.changeTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(team);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(team2);
            em.persist(member3);

            em.flush();
            em.clear();

            // JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
            // select count(m.id) from Member m - 엔티티의 아이디를 사용
            // select count(m) from Member m - 엔티티를 직접 사용
            // 둘다 select count(m.id) as cnt from Member m
            System.out.println("=============");
            Member findMember = em.createQuery("select m from Member m where m = :member", Member.class)
                    .setParameter("member", member).getSingleResult();

            System.out.println("findMember = " + findMember);

            // 엔티티 직접사용 외래 키 값 사용
            List<Member> findMemberList = em.createQuery("select m from Member as m where m.team = :team", Member.class)
                    .setParameter("team", team)
                    .getResultList();

            for (Member member1 : findMemberList) {
                System.out.println("member1 = " + member1);
            }

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
    void namedQueryTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("팀A");
            em.persist(team);

            Team team2 = new Team();
            team2.setName("팀B");
            em.persist(team2);

            Member member = new Member();
            member.setUsername("회원1");
            member.changeTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(team);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(team2);
            em.persist(member3);

            em.flush();
            em.clear();

            // Named 쿼리 - 정적 쿼리
            // - 미맂 정의해서 이름을 부여해두고 사용하는 JPQL
            // - 정적 쿼리
            // - 어노테이션, XML에 정의
            // - 애플리케이션 로딩 시점에 초기화 후 재사용
            // - 애플리케이션 로딩 시점에 쿼리를 검증

            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member findMember : resultList) {
                System.out.println("member1 = " + findMember);
            }

            // XML이 우선권을 가진다.
            // Spring Data JPA 항목에서 @Query 항목에서 인터페이스에서 사용한다.
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
    void bulkOperationTest(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 벌크연산
            // 재고가 10개 미만인 모든 상푸믜 가격을 10% 상승하려면 ?
            // JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행
            // executeUpdate를 실행
            Member member = new Member();
            member.setUsername("회원1");
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            em.persist(member3);

            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            em.flush();
            em.clear();
//
            System.out.println("resultCount = " + resultCount);

            // 벌크 연산 주의
            // 벌크 연사은 영속성 컨텐스트를 무시하고 데이터베이스를 직접 쿼리
            // 벌크 연산을 먼저 실행
            // 벌크 연산 수행 후 영속성 컨텐스트 초기화

            System.out.println("member.getAge() = " + member.getAge());
            System.out.println("member2.getAge() = " + member2.getAge());
            System.out.println("member3.getAge() = " + member3.getAge());

            Member findMember = em.find(Member.class, member.getId());
            System.out.println("findMember.getAge() = " + findMember.getAge());

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
