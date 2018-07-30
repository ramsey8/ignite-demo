package liu.york.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.cloudwalk.basic.ignite.config.IgniteSessionConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/session")
public class Controller {

	@Resource
	private IgniteSessionConfiguration igniteSessionConfiguration;
    @RequestMapping("/fun1")
    public void fun1(HttpServletRequest request){
        System.out.println("request coming ...");
        request.getSession().setAttribute("user","liqiang");
        String sessionId = request.getSession().getId();
        System.out.println("sessionId ===============> " + sessionId);
        Object obj=igniteSessionConfiguration.getIgnite().getOrCreateCache("ignite_session_cache").get(sessionId);
        System.out.println("Done.");
    }
}