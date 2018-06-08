package liu.york.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/session")
public class Controller {

    @RequestMapping("/fun1")
    public void fun1(HttpServletRequest request){
        System.out.println("request coming ...");

        String sessionId = request.getSession().getId();
        System.out.println("sessionId ===============> " + sessionId);

        System.out.println("Done.");
    }
}