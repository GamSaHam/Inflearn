package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Data 에서 ToString  자동 생성해준다.
public class MemberDto {

    private String username;
    private int age;

    // @QueryProjection
    // Q파일 생성하는것
    // DTO QueryDSL 라이브러리에 의존적이다.
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
