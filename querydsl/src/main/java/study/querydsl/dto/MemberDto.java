package study.querydsl.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Data 에서 ToString  자동 생성해준다.
public class MemberDto {

    private String username;
    private int age;

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public MemberDto() {
    }

    @Override
    public String toString() {
        return "MemberDto{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
