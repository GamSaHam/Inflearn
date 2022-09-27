package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        // give
        Member member = new Member("회원A");
        Member savedMember = memberJpaRepository.save(member);

        // when
        Member findMember = memberJpaRepository.find(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());

        // 같은 트렌젝션 영속성 컨텐스트에서 1차캐시에서는 동일값을 보장
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member1.getId()).get();

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }


    @Test
    public void findByUsernameAndAgeGreaterThenTest() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberJpaRepository.save(userA);
        memberJpaRepository.save(userA);

        //then
        List<Member> findMembers = memberJpaRepository.findByUsernameAndAgeGreaterThen("userA", 5);

        assertThat(findMembers.get(0).getUsername()).isEqualTo("userA");
        assertThat(findMembers.get(0).getAge()).isEqualTo(10);
        assertThat(findMembers.size()).isEqualTo(1);
    }


    @Test
    public void testNamedQuery() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberJpaRepository.save(userA);
        memberJpaRepository.save(userA);

        //then
        List<Member> result = memberJpaRepository.findByUsername("userA");

        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(userA);
    }

    @Test
    public void paging() {
        for(int i=1;i<=10;i++) {
            memberJpaRepository.save(new Member("member" + i, 10));
        }

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> pagedMember = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(10);

        // 페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ...

        // then
        assertThat(pagedMember.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(10);
    }

    @Test
    public void bulkUpdate() {
        // given
        for(int i=1;i<=10;i++) {
            memberJpaRepository.save(new Member("member" + i, i));
        }

        // when
        int row = memberJpaRepository.bulkAgePlus(5);

        // then
        assertThat(row).isEqualTo(6);
    }

}