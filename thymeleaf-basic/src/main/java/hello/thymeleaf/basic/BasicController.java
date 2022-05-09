package hello.thymeleaf.basic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/text-basic")
    public String textBasic(Model model) {

        // html 엔티티를 escape 를 처리한다 [[...]] 항목에서는
        model.addAttribute("data", "Hello <b>Spring</b>!");

        return "basic/text-basic";
    }

    @GetMapping("/text-unescaped")
    public String textUnescaped(Model model) {

        // html 엔티티를 escape 를 처리한다 [[...]] 항목에서는
        model.addAttribute("data", "Hello <b>Spring</b>!");

        return "basic/text-unescaped";
    }
}
