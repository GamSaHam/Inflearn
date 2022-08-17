package hellopja;

import org.junit.jupiter.api.Test;

class EntityTest {

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



}