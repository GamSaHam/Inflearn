package hellopja;


import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // member클래스에 team 변수랑 매핑이 된다라고 명시
    // 객체와 테이블이 관계를 맺는 차이
    // 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개다.
    // 테이블의 양방향 연관관계는 외래키 하나로 두 테이블의 연관관계를 관리
    // Member , Team 이 연관관계으ㅢ 주인만이 외래 키를 관리 등록
    // 주인이 아닌쪽은 읽기만 가능
    // 주인이 아닌쪽이 MappedBy로 지정
    // 외래 키가 있는 곳으로 주인을 정해라
    // oneToMany 쪽에 가짜매핑이 MappedBy로 지정한다.
    private List<Member> members = new ArrayList<>(); // 빌더 패턴 안써도 된다.

    public Team(String name) {
        this.name = name;
    }

    public Team() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }
}
