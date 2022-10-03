package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // Open Projections 전체 select 에서 전체 데이터를 가지고 온다.
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
