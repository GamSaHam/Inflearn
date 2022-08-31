package hellopja;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
//@SequenceGenerator(name="member_seq_generator" , sequenceName = "member_seq")
public class Member extends BaseEntity{
    @Id // 기본키 매핑
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String userName;
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member( String userName) {
        this.userName = userName;
    }

//    @Enumerated(EnumType.STRING)
//    private RolType rolType;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdDate;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;
//
//    @Lob
//    private String description;
//
//    // 메모리에서만 사용
//    @Transient
//    private int temp;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private  Locker locker;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

//    public void changeTeam(Team team) {
//        // ** 항목에 실수를 방지하기 위해 적용된 코드
//        this.team = team;
//
//        // 연관관계 편의 메서드
//        team.getMembers().add(this);
//    }
}



