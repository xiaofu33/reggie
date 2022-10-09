package com.shuke.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuke.reggie.common.R;
import com.shuke.reggie.entity.User;
import com.shuke.reggie.service.UserService;
import com.shuke.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){


        // 获取手机号
        String phone = user.getPhone();

        if(!StringUtils.isEmpty(phone)){
            // 生成随机的 4 位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为 ：{}",code);

            // 保存验证码到 Session 中
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }


    /**
     * 用户登录：身份验证功能
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String codeInSession = (String)session.getAttribute(phone);

        if(codeInSession != null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
