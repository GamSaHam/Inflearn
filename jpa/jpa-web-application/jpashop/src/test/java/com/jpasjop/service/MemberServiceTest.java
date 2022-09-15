package com.jpasjop.service;

import com.jpasjop.domain.Member;
import com.jpasjop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
//    @Rollback(false)
    public void joinTest() {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);

        // 같은 영속성 컨텐스트에서 관리가 되어서 같은값이 나온다.

        // 롤백 항목으로 인해 인서트 쿼리가 들어가지 않는다.
        // 드래서 강제로 호출을 해줘야 한다.
        em.flush();

        // then
        Assertions.assertThat(member).isEqualTo(memberRepository.findOne(savedId));

        // was 에서 인메모리 디비를 연결하면 좋다.
    }


    @Test
    public void duplicateMemberTest() {
        // given
        Member member1 = new Member();
        member1.setName("kim1");


        Member member2 = new Member();
        member2.setName("kim1");

        memberService.join(member1);

        // then
        Assertions.assertThatThrownBy(() -> {
            memberService.join(member2);}).isInstanceOf(IllegalArgumentException.class);

    }




}