package com.hsb.community.controller;

import com.hsb.community.entity.DiscussPost;
import com.hsb.community.entity.Page;
import com.hsb.community.entity.User;
import com.hsb.community.service.DiscussPostService;
import com.hsb.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        //
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model. (方法的参数)
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> postList = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(!postList.isEmpty()) {
            for(DiscussPost post:postList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserByID(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
