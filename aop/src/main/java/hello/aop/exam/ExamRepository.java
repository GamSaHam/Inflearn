package hello.aop.exam;

import hello.aop.exam.annotation.Retry;
import hello.aop.exam.annotation.Trace;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Retention;

@Repository
public class ExamRepository {


    private static int seq = 0;


    @Trace
    @Retry(4)
    public String save(String itemId) {
        seq++;

        if(seq % 5 ==0) {
            throw new IllegalStateException("예외발생");
        }

        return "ok";

    }



}
