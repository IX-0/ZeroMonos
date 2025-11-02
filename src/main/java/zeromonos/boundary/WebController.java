package zeromonos.boundary;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/client")
    public String clientPage() {
        return "client";
    }

    @GetMapping("/employee")
    public String employeePage() {
        return "employee";
    }
}
