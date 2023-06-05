package com.hsb.community.service;

import com.hsb.community.dao.UserMapper;
import com.hsb.community.entity.User;
import com.hsb.community.util.CommunityConstant;
import com.hsb.community.util.CommunityUtil;
import com.hsb.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserByID(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //  空值处理
        if(user==null)
        {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername()))
        {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword()))
        {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail()))
        {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        //  验证账号、邮箱是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null)
        {
            map.put("usernameMsg", "账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null)
        {
            map.put("emailMsg", "邮箱已被注册！");
            return map;
        }

        //  数据合法，注册账户
        //  密码加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //  未激活
        user.setStatus(0);
        //  用户类型
        user.setType(0);
        //  激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //  随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //  创建时间
        user.setCreateTime(new Date());
        //  用户插入数据库
        userMapper.insertUser(user);

        //  发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //  http://localhost:8080/community/activation/101/code   用户id和激活码
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }
}
