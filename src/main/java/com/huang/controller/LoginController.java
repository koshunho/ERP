package com.huang.controller;

import com.huang.pojo.User;
import com.huang.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session){
        //User user = userService.selectUserByName(username, password);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try{
            subject.login(token);
            session.setAttribute("loginUser",username);
            return "redirect:/main.html";
        }catch (UnknownAccountException e){
            model.addAttribute("msg","用户名错误！");
            return "index";
        }catch(IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误！");
            return "index";
        }

        // if(user!=null){
        //     session.setAttribute("loginUser",username);
        //     return "redirect:/main.html";
        // }else{
        //     model.addAttribute("msg","用户名或密码错误！");
        //     return "index";
        // }
    }

    @RequestMapping("/user/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/index.html";
    }
}
