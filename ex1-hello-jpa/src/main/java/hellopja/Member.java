package hellopja;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@SequenceGenerator(name="member_seq_generator" , sequenceName = "member_seq")
public class Member {
    @Id // 기본키 매핑
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private Integer age;


    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    @Enumerated(EnumType.STRING)
    private RolType rolType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    // 메모리에서만 사용
    @Transient
    private int temp;

}



