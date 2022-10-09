package com.shuke.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.shuke.reggie.common.BaseContext;
import com.shuke.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 1.获取请求地址
        String requestURL = request.getRequestURI();

        // 2.设置不需要拦截的请求地址
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common",
                "/user/sendMsg",
                "/user/login"
        };

        // 3.判断请求地址是否需要拦截（与上面的 urls 匹配）
        boolean check = check(urls,requestURL);

        // 无需拦截
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        // 已登录，无需拦截（后台管理）
        if(request.getSession().getAttribute("employee") != null){

            // 将用户 id 储存到线程变量中
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // 已登录，无需拦截（客户端）
        if(request.getSession().getAttribute("user") != null){

            // 将用户 id 储存到线程变量中
            Long empId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // 未登录，拦截并返回结果，通过输出流向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    // 地址匹配
    public boolean check(String[] urls,String requestURL){
        for(String url : urls){
            if(PATH_MATCHER.match(url,requestURL)){
                return true;
            }
        }
        return false;
    }
}
