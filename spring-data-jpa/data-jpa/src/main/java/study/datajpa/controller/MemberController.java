package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터
    // 권장하지 않음
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    // @PageableDefault(size = 5)
    public Page<MemberDto> list(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);

//        Page<MemberDto> map = page.map(member -> {
//            return new MemberDto(member);
//        });

        // 메서드 레퍼런스로 바꿀 수 있다.
        Page<MemberDto> map = page.map(member -> new MemberDto(member));

        // DTO(Data Transfer Object를 반환해야 한다.


        return map;
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

}
