package ee.anton.quizgame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({ "/", "" })
    public String index() {
        // forward to static index.html in classpath:/static/index.html
        return "forward:/index.html";
    }
}
