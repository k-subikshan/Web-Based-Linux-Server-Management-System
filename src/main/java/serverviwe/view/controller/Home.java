package serverviwe.view.controller;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class Home {
    
    @GetMapping("/")
    public String Homeview() {

        return "Hello " ;
    }
    

}
