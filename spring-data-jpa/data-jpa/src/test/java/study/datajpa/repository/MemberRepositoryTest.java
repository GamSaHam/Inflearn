package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        // 프록시 기술을 활용하여 Spring Data JPA 구현체를 의존성 주입을 해준다.
        System.out.println("memberRepository = " + memberRepository.getClass());

        // give
        Member member = new Member("회원A");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member1.getId()).get();

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

        // 스프링 데이터
        // Repository
        // CrudRepository
        // PagingAndSortingRepository
        // 스프링 데이터 JPA
        // JpaRepository
        // 공통 기술을 추상화 시켰다.

        // findByUsername -- 쿼리 메서드 기능

        // getOne:
        // getReference 프록시 객체 가지고 온다.
    }


    @Test
    public void findByUsernameAndAgeGreaterThenTest() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userA);

        //then
        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("userA", 5);

        assertThat(findMembers.get(0).getUsername()).isEqualTo("userA");
        assertThat(findMembers.get(0).getAge()).isEqualTo(10);
        assertThat(findMembers.size()).isEqualTo(1);

        // 메서드 이름으로 인터페이스를 생성해 준다.
        // COUNT, EXISTS, DISTINCT, LIMIT
        // findTop3HelloBy()

        // Named Query는 사용하지 않는다.
    }

    @Test
    public void testNamedQuery() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userA);

        //then
        List<Member> result = memberRepository.findByUsername("userA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(userA);

        // Named Query 항목은 엔티티 항목에 추가 되어있는게 좋지 않다.
        // 스프링 데이터 JPA 제공하는 Named 쿼리 항목이 있기 때문에 이기능을 활용한다.
        // Named Query 에서는 애플리케이션 로딩시점에 오류를 알려준다.
    }



    @Test
    public void testQuery() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userA);

        //then
        List<Member> result = memberRepository.findUser("userA", 10);

        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(userA);

        // 복잡한 정적 쿼리를 사용한다.
        // 쿼리 Query DSL을 사용한다.
    }

    @Test
    public void findUserNameListTest() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userA);

        //then
        List<String> result = memberRepository.findUserNameList();

        for (String name : result) {
            System.out.println("name = " + name);
        }
    }

    @Test
    public void findMemberDtoTest() throws Exception {
        // given
        // when
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member userA = new Member("userA", 10);
        userA.changeTeam(team);
        memberRepository.save(userA);

        //then
        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNamesTest() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);

        memberRepository.save(userA);
        memberRepository.save(userB);

        //then
        List<Member> members = memberRepository.findByNames(Arrays.asList("userA", "userB"));

        for (Member member : members) {
            System.out.println("member = " + member);
        }
        // 위치기반 이름기반으로 파라미터 전달 가능
    }

    @Test
    public void returnTypeTest() throws Exception {
        // given
        // when
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userB);

        //컬렉션은 빈 켤렉션을 제공한다. 리턴값을 null값을 제공하지 않는다.
        List<Member> members = memberRepository.findListByUsername("userA");
        // 단건은 null 값을 리턴한다. JPA NoResultException 는 에러를 호출하는데 Spring Data JPA 에서는 try {} catch() {}로 잡는다.
        // 자바 8 이후에서는 Optional로 지정해서 반환한다.

        // 이름이 두개일 경우에는 에러가 발생한다.
        Member member = memberRepository.findMemberByUsername("userA");
        Optional<Member> memberOptional = memberRepository.findOptionalByUsername("userA");

        for (Member result : members) {
            System.out.println("result = " + result);
        }

        System.out.println("member = " + member);
        System.out.println("memberOptional.get() = " + memberOptional.get());
    }

    @Test
    public void paging() {
        for(int i=1;i<=10;i++) {
            memberRepository.save(new Member("member" + i, 10));
        }

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(10, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(10, pageRequest);

//        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();

        // then
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        // total 카운트에서 조회가 느려진다.
        // Slice Total Count 항목이 안날라간다.

        // 성능 테스트가 느릴경우 카운트 쿼리를 지정할 수 있다.
    }

    @Test
    public void bulkUpdate() {
        // given
        for(int i=1;i<=10;i++) {
            memberRepository.save(new Member("member" + i, i));
        }

        // when
        int row = memberRepository.bulkAgePlus(5);
//        em.clear();

        // 영속성 컨텍스트에서 관리를 안하고 바로 때려 버린다.
        // 벌크 연산후에 모두 날려버려야한다.

        // 영속성 컨텍스트에서 member 에 1차캐시가 10개 들어가 있다.
        List<Member> result = memberRepository.findByUsername("member5");

        Member member = result.get(0);
        System.out.println("member = " + member);

        // then
        assertThat(row).isEqualTo(6);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        
        // when
        List<Member> findMembers = memberRepository.findAll();

        // LAZY 속성으로 발생할수있는 프록시 조회시 디비가 나가는 N + 1 문제에 대해서
        // Fetch 조인으로 해결을 한다.
        System.out.println("===================");
//        List<Member> findMembers = memberRepository.findMemberFetchJoin();

        // EntityGraph 로 어노테이션 하면 Fetch 조인이 가능하다.
//        List<Member> findMembers = memberRepository.findAll();
//        List<Member> findMembers = memberRepository.findEntityGraphByUsername("member1");

        for (Member findMember : findMembers) {
            System.out.println("findMember.getUsername() = " + findMember.getUsername());
        }
        System.out.println("===================");
    }

    @Test
    public void queryHint() {
        // given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // 스냅샷에서 변경을 무시해 버린다.

        // 이런것을 적용하기 위해 QueryHint 를 제공해 준다. 잘 사용안하다.

        // 전체 100중에서 복잡한 쿼리가 나가서 느려지는 건다.
        // ReadOnly 항목 중에 느리게 발생 하는 것은 그렇게 크기 않다.
        // 이미 레디스 캐시를 넣어야한다. 느려질 경우
        em.flush();
    }

    @Test
    public void lockTest() {
        // given
        Member member = memberRepository.save(new Member("member1", 10));

        em.flush();
        em.clear();

        //when
        // 락 구문은 책에 구문에서 참고를 해야한다.
        // 실시간 트래픽이 많으면 락을 걸어버리면 안된다.
        // 돈관련된거는 락을 걸어서 정확하게 해야한다.
        List<Member> result = memberRepository.findLockByUsername("member1");
    }



}