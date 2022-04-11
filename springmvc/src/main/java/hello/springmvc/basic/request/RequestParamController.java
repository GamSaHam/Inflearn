package hello.springmvc.basic.request;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok");
    }

    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamsV2(@RequestParam("username") String memberName,
                                  @RequestParam("age") int memberArg) {

        log.info("username={}, age={}", memberName, memberArg);
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamsV3(@RequestParam String username,
                                  @RequestParam int age) {

        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamsV4(String username,
                                  int age) {

        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * usernmae 항목에 required true 임에도 불구하고 "" 처리만 진행한다.
     * @param username
     * @param age
     * @return
     */
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamsRequired(@RequestParam(required = true) String username,
                                  @RequestParam(required = false) Integer age) {

        log.info("username={}, age={}", username, age);
        return "ok";
    }

    /**
     * defaultValue 들어가면 required 항목이 필요없어진다.
     * 빈문자인경우에도 처리가 된다.
     * @param username
     * @param age
     * @return
     */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamsDefault(@RequestParam(required = true, defaultValue="guest") String username,
                                        @RequestParam(required = false, defaultValue = "-1") Integer age) {

        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamsMap(@RequestParam Map<String , Object> paramMap) {
        log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
        return "ok";
    }

    /**
     * HelloData 객체를 생성한다. HelloData 객체 프로퍼티를 찾는다. setter 를 호출하여 바인딩한다.
     * @param helloData
     * @return
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }





}






