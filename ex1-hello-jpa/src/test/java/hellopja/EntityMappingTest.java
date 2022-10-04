package hellopja;

import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

class EntityMappingTest {

    @Test
    void entityTest() {
        // @Entity, @Table
        // @Column
        // @Id

        // @Table(name = "") // 데이터 베이스 매핑 ,

        // DDL 은 개발에서만 방언을 통해서 자료형이 자동으로 매칭된다.

        // hibernate.hbm2ddl.auto
        // create - 기존 테이블 삭제후 생성
        // create-drop - 종료 시점에 테이블 드롭
        // update // 변경분만 반영
        // validate // 엔티티와 테이블이 정상 매핑되었는지만 확인
        // none // 사용하지 않음

        // 개발서버 create, update
        // 테스트서버 update, validate // update도 lock 이 걸린다.
        // 운영서버 validate, none

        // 개발서버에서도 none 이나음 로컬피시에서만 사용함
        // DDL 생성기능
        // @Column(name="", unique = true, length = 10)
    }

    @Test
    void fieldTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // insertable, updatable
            // nullable
            // unique - 잘 안쓴다. 랜덤값으로 유니크값으로 생성하기때문에 운영에서 처리하기 힘듬
            // 클래스 위에서 한다.
            // length
            // columnDefinition = "varchar(100) default 'EMPTY'"
            // BigDecimal , precious

            // EnumType.ORDINAL 순서를 데이터베이스저장, STRING 이름을 데이터 베이스에 저장
            Member member = new Member();
            member.setId(1L);
            member.setUsername("A");
//            member.setRolType(RolType.USER);

            em.persist(member);

            // LocalDate -> Date
            // LocalDateTime  -> TimeStamp

            // clob: 문자
            // blob: 나머지

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
    public void primaryKey() {

        // GernernateValue
        // AUTO
        // IDENTITY (My SQL)
        // SEQUENCE (Oracle)
        // TABLE ()
        // SequenceGenerator

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("C");

            System.out.println("===================");
            // IDENTITY전략을 사용하면 INSERT INFO 항목을 실행 한다.
            em.persist(member);

            System.out.println("member.id = " + member.getId());
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