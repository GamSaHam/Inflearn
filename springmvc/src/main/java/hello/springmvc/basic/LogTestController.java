package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {

//    private final Logger log = LoggerFactory.getLogger(getClass());


    @RequestMapping("/log-test")
    public String logTest() {
        String name = "String";
        System.out.println("name = " + name);

        log.trace("trace log=" + name); // 자바 컴파일러가 "trace log=" + "String" 으로 치환 후
        // "trace log=String" 으로 연산이 이루어집
        // 메모리랑 cpu를 사용한다. trace 레벨에서는 출력이 안되는 것이므로 메모리랑 cpu를 잡아먹는게 문제

        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info("info log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        // debug 개발서버
        // trace 로컬서버
        // info 운영서버

        //System.out.println 은 로컬, 개발, 운영을 따로 두지 않는다.

        return "ok";

    }



}
