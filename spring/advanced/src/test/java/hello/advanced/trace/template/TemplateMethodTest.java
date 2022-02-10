package hello.advanced.trace.template;

import hello.advanced.trace.template.code.AbstractTemplates;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {


    @Test
    void templateMethodV0() {
        logic1();
        logic2();

    }

    private void logic1() {

        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    private void logic2() {

        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }



    // 익명 내부 클래스 활용
    @Test
    void templateMethodV2() {

        AbstractTemplates template1 = new AbstractTemplates() {

            @Override
            protected void call() {
                log.info("비즈니스 로직 1 실행");
            }
        };

        log.info("클래스 이름1={}", template1.getClass());
        template1.execute();

        AbstractTemplates template2 = new AbstractTemplates() {

            @Override
            protected void call() {
                log.info("비즈니스 로직 2 실행");
            }
        };
        template2.execute();



    }












}
