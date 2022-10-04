package hellopja;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ValueTypeTest {
    @Test
    void primitiveTypeTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 엔티티 타입
            // 데이터가 변해도 식별자로 지속해서 추적 가능

            // 값 타입
            // - 자바 기본 타입이나 객체
            // - 식별자가 없고 값만 있으므로 변경시 추적 불가

            // 기본값타입
            // 생명주기를 엔티티의 의존, 값 타입은 공유하면 X(공유가 안되다)
            // - 자바 타입(int, double)
            // - 래퍼 클래스(Integer, Long)
            // - String

            // 임베디드 타입(embedded type, 복합 값 타입)
            //  새로운 값 타입을 직접 정의할 수 있음
            // JPA 임베디드 타입이라함
            // 기본 값 타입을 모아서 복합 값 타입이라고도 함
            // int, String 값 타입이다.
            // ex) 좌표
            // 장점(재사용, 높은 응집도) isWork() 메소드를 만들수있음
            // 값타입은 엔티티에 생성주기를 의존함
            // 객체와 테이블을 아주 세밀하게 패핑하는 것이 가능
            // 잘 설계한 ORM 애플리케이션은 매핑한 테ㅣ블의 수보다 클래스의 수가 더 많음
            // 그렇다고 많이 쓰이지는 않는다.
            // 도메인의 언어를 공통화 할수 있다.

            // 컬랙션 값 타입(collection value type)
            // 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다.

            // 입베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함

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
    void embeddedTypeTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Address address = new Address("city", "street", "zipcode");

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
            em.persist(member);

            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setHomeAddress(copyAddress); // 컴파일러 레벨에서 address 을 넣을경우 찾을수없다.
            em.persist(member2);

            // 이런 종류에 버그는 잡기 어렵다.
//            member.getHomeAddress().setCity("newCity");


            // 불변하게 설정 (final, setter를 삭제)을 하여 새로 만들어야 한다.
            member.setHomeAddress(new Address("NewCity", address.getStreet(), address.getZipcode()));

            // 임베디트 타입을 여러 엔티티에서 공유하면 위험함
            // 공유 참조로 인해 발생하는 부작용을 피할 수 있다.

            // 객체는 공유참조를 피할수 없다.
            // 객체 타입은 한계가 있다.
            // 객체 타입을 수정할수 없게 만들면 부작용을 원천 차단할수있다.
            // 초기화이후 값을 변경할수 없는 객체로 변경
            // 생성자로만 값을 설정하고 수정자를(Setter)를 만들지 않으면 된다.

            // 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다.
            // 값타입은 불변으로 만들어야 한다.

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
    void embeddedTypeEqualTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 객체타입 비교
            int a = 10;
            int b = 10;

            System.out.println("a == b = " + (a == b));

            Address address1 = new Address("city", "street", "zipcode");
            Address address2 = new Address("city", "street", "zipcode");

            System.out.println("address1 == address2 = " + (address1 == address2));
            System.out.println("address1.equals(address2) = " + (address1.equals(address2)));

            // 동일성 비교, 참조값 ==
            // 동등성 비교, 인스턴스 값 비교 equals
            // embedded 타입은 equals를 사용해줘야한다.

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
    void embeddedTypeCollectionTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 객체타입 비교
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("city", "street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("city2", "street2", "zipcode2"));
            member.getAddressHistory().add(new AddressEntity("city3", "street3", "zipcode3"));

            em.persist(member);

            em.flush();
            em.clear();
            System.out.println("=============");

            // 값타입이라서 소유에 영속성 전이(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 보면 된다.
            // 컬랙션 항목은 지연로딩이 된다.
            Member findMember = em.find(Member.class, member.getId());

//            List<Address> addressHistory = findMember.getAddressHistory();
//
//            for (Address address : addressHistory) {
//                System.out.println("address.getCity() = " + address.getCity());
//            }
//
//            Set<String> favoriteFoods = findMember.getFavoriteFoods();
//
//            for (String favoriteFood : favoriteFoods) {
//                System.out.println("favoriteFood = " + favoriteFood);
//            }

            // 치킨을 한식으로 바꾼다.
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

//            findMember.getAddressHistory().remove(new AddressEntity("city2", "street2", "zipcode2"));
//            findMember.getAddressHistory().add(new AddressEntity("city2-1", "street2", "zipcode2"));

            // delete를 전부다 하고
            // addHistory 항목을 전부 추가한다.

            // 값타입은 엔티티와 다르게 식별자가 없다.

            // 값타입 컬렉션이 변경이 발생하면, 주인엔티티와 연관된 모든 데이터를 삭제하고
            // 값 타입 컬렉션에 있는 현재값을 모두 저장한다.

            // 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
            // 일다대 관계는 CaseCade Orphan항목으로 지정한다
            // 값 항목을 entity로 승급한다고 한다.
            // 진짜 단순할때 사용한다 ex) checkbox 항목

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
