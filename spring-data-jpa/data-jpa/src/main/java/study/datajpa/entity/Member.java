package study.datajpa.entity;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
