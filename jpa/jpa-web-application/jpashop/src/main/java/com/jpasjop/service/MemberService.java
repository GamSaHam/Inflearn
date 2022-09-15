package com.jpasjop.service;

import com.jpasjop.domain.Member;
import com.jpasjop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 아래와 같이 사용을 해야 테스트 코드로 목 인잭션 하기가 편하다
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     * @param member
     * @return
     */
    @Transactional
    public Long join(Member member){

        // 중복회원 가입
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 웹에서는 동시성 문제가 발생할수 있다.
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if(findMembers.isEmpty() == false) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 정보 조회 , 읽기전용 트렌젝션에 최적화를 시킨다. Duty 체킹으로 인한.
     * @return
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }


}
