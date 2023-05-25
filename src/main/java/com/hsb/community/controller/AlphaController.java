package com.hsb.community.controller;

import com.hsb.community.config.AlphaConfig;
import com.hsb.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//  1次请求的执行过程
//  浏览器 --> (controller --> service --> DAO) --> DB
//            {          server              }
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private AlphaConfig alphaConfig;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello()
    {
        return "Hello World!";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData()
    {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
//        获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath()); //请求路径
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()){
            String name = names.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+": "+value);
        }
        System.out.println(request.getParameter("code")); // 业务数据传的参数 .../http?code=123
//        返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter();
                ){
            writer.write("<h1>论坛<h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

//    GET请求（两种传参方式）
//    /students?current=1&limit=20
    @RequestMapping(path="/students", method = RequestMethod.GET) // 只能接收GET请求
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "1") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //  GET请求
    //  /students/123
    @RequestMapping(path = "student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //  POST请求  上传表单数据，打印出数据
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //  响应HTML数据（动态）
    @RequestMapping(path = "teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","qwe");
        modelAndView.addObject("age",33);
        modelAndView.setViewName("/demo/view"); //  模板位置
        return modelAndView;
    }

    //  响应HTML数据（动态）
    @RequestMapping(path = "school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","xmu");
        model.addAttribute("age",100);
        return "/demo/view";//view路径
    }

    //  响应JSON数据（异步请求(网页不刷新)）
    //  Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody  //   不加默认返回的是HTML
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","hhh");
        emp.put("age",23);
        emp.put("salary",1000000);
        return emp;
    }

    //  返回多个相似数据
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody  //   不加默认返回的是HTML
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","hhh");
        emp.put("age",23);
        emp.put("salary",1000000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","hhh1");
        emp.put("age",23);
        emp.put("salary",1000000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","hhh2");
        emp.put("age",23);
        emp.put("salary",1000000);
        list.add(emp);

        return list;
    }




}
