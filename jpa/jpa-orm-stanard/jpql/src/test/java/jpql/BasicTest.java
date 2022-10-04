package jpql;

import jpql.domain.*;
import jpql.dto.MemberDTO;
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

    @Test
    void projectionTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 프로젝션 SELECT  절에 조회할 대상을 지정하는것
            // 엔티티, 임베디드, 스칼라
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();

            Member findMember = result.get(0);
            findMember.setAge(20);

            // result 영속성 컨텍스트에서 관리된다.

            // 조인으로 관리가 된다.
//            List<Team> teamResult = em.createQuery("select m.team from Member m", Team.class).getResultList();
            List<Team> teamResult = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();

            // 값타입에 한계
            em.createQuery("select o.address from Order o", Address.class).getResultList();

            // 스칼리
            List<Object[]> scalarResultList = em.createQuery("select m.username, m.age from Member m").getResultList();

            Object[] scalarResult = scalarResultList.get(0);
            System.out.println("username = " + scalarResult[0]);
            System.out.println("age = " + scalarResult[1]);

            List<MemberDTO> scalarResultDtoList = em.createQuery("select new jpql.dto.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            System.out.println("username = " + scalarResultDtoList.get(0).getUsername());
            System.out.println("age = " + scalarResultDtoList.get(0).getAge());


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
    void pagingTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);

            }

            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("resultList.size() = " + resultList.size());
            for (Member findMember : resultList) {
                System.out.println("findMember = " + findMember);
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
    void joinTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 내부조인
            // select m from Member m [inner] join m.team t
            // 외부조인
            // select m from Member m left [OUTER] join m.team t
            // 세타조인
            // select count(m) from Member m, Team t where m.username = t.name
            Team team = new Team();
            team.setName("a");
            em.persist(team);

            Member member = new Member();
            member.setUsername("a");
            member.setAge(10);
            // 연관관계 편의 메서드
            member.changeTeam(team);

            em.persist(member);

            em.createQuery("select m from Member m join m.team t", Member.class).getResultList();
            em.createQuery("select m from Member m left join m.team t", Member.class).getResultList();
            em.createQuery("select m from Member m, Team t where m.username = t.name", Member.class).getResultList();

            // 회원과 팀을 조인하는데 팀 이름이 A인 팀만 조회
            em.createQuery("select m from Member m left join Team t on m.username = t.name", Member.class).getResultList();


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
    void subqueryTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 내부조인
            // select m from Member m [inner] join m.team t
            // 외부조인
            // select m from Member m left [OUTER] join m.team t
            // 세타조인
            // select count(m) from Member m, Team t where m.username = t.name
            Team team = new Team();
            team.setName("a");
            em.persist(team);

            Member member = new Member();
            member.setUsername("a");
            member.setAge(10);
            // 연관관계 편의 메서드
            member.changeTeam(team);

            em.persist(member);

            // 일반적인 sql에서 되는 서브쿼리가 된다.
            em.createQuery("select m from Member m where m.age > (select avg(m2.age) from Member m2)", Member.class).getResultList();

            // 서브 쿼리 지원 함수 exist
            em.createQuery("select m from Member m  where exists (select t from m.team t where t.name = 'a')", Member.class);

            // 전체 상품 각각의 재고보다 주문량이 많은 주문들
            em.createQuery("select o from Order o where o.orderAmount > ALL(select p.stockAmount from Product p)", Order.class);

            // 어떤 팀이든 팀에 소속된 회원
            em.createQuery("select m from Member m where m.team = ANY(select t from Team t)", Member.class);

            // JPA where ,having 절에서 만사용가능
            // 하이버네이트에서 지원
            // from 절의 서브 쿼리는 현재 JPQL에서 불가능
            // 조인으로 풀 수 있으면 풀어서 해결 - 조인으로 풀 수 있으면 풀어서 해결

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
    void typeTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // JPQL 타입 표현
            // 문자, 숫자, 불린, enum, 엔티티 타입
            Team team = new Team();
            team.setName("a");
            em.persist(team);

            Member member = new Member();
            member.setUsername("a");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            // 연관관계 편의 메서드
            member.changeTeam(team);
            em.persist(member);

            String qlString = "select m.username, 'HELLO' from Member m where m.type = :userType";

            List<Object[]> resultList = em.createQuery(qlString)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : resultList) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
            }

            // JPQL 기타
            // SQL EXISTS, IN,s AND, OR, NOT, =, >, >=, <, <=, <>
            // BETWEEN, LIKE, IS NULL

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
    void caseTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // JPQL 타입 표현
            // 문자, 숫자, 불린, enum, 엔티티 타입
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

            String queryString =
                    "select case when m.age <= 10 then '학생요금' " +
                            "when m.age >= 60 then '경로요금'" +
                            "else '일반요금' end" +
                            " from Member m ";
            em.createQuery(queryString).getResultList();

            // coalesce()
            // 첫번째 인자가 널일 경우 두번째 인자로 반환
            em.createQuery("select coalesce(m.username, '이름 없는 회원') from Member m").getResultList();

            // nullif 1번째 인자가 2번째 인자랑 같은면 널을 반환
            List<String> resultList = em.createQuery("select nullif(m.username, '관리자') from Member m ").getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
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
    void basicMethodTest() {
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

            // JPQL에서 지원하는 기본 함수
            // concat, substring, trim, lower, upper, length, locate, abs, sqrt, mod,
            // size, index (JPA 용도)

            // 사용자 정의함수
            // select function('group_concat', i.name) from Item i

            em.createQuery("select concat('a', 'b') from Member m").getResultList();
            em.createQuery("select substring(m.username, 0, 5) from Member m").getResultList();
            // locate은 1부터 시작
            em.createQuery("select locate('bc', 'abcde') from Member m").getResultList();
            em.createQuery("select size(t.members) from Team t").getResultList();

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
