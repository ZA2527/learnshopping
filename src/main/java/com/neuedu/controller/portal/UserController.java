package com.neuedu.controller.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/portal/user/")
public class UserController{

    @Autowired
    IUserService userService;

    @RequestMapping(value = "login.do")
    public ServerResponse login(HttpSession session,String username, String password){
       ServerResponse serverResponse = userService.login(username,password);
       if(serverResponse.isSuccess()){
//           保存登录状态
           /*Const.CURRENTUSER 代表用户表中的字段名
           serverResponse.getData() 代表字段中所对应的值*/
           session.setAttribute(Const.CURRENTUSER,serverResponse.getData());
       }
       return serverResponse;
    }

    /***
     * 注册
     * @return
     */
    @RequestMapping(value = "register.do")
    public ServerResponse register(UserInfo userInfo){
        return userService.register(userInfo);
    }

    /**
     * 检测用户名或邮箱是否有效
     * */
    @RequestMapping(value = "check_valid.do")
    public ServerResponse check_valid(String str,String type){
        return userService.check_valid(str,type);
    }

    /**
     * 获取登录用户信息
     * */
    @RequestMapping(value = "get_user_info.do")
    public ServerResponse get_user_info(HttpSession session){
        Object o = session.getAttribute(Const.CURRENTUSER);
        if(o!=null&& o instanceof UserInfo){
            UserInfo userInfo = (UserInfo) o;
            UserInfo responseUserInfo = new UserInfo();
            responseUserInfo.setId(userInfo.getId());
            responseUserInfo.setUsername(userInfo.getUsername());
            responseUserInfo.setEmail(userInfo.getEmail());
            responseUserInfo.setCreateTime(userInfo.getCreateTime());
            responseUserInfo.setUpdateTime(userInfo.getUpdateTime());
            return ServerResponse.createServerResponseBySuccess(null,responseUserInfo);
        }
        return ServerResponse.createServerResponseByError(ResponseCode.USER_NOT_LOGIN.getStatus(),ResponseCode.USER_NOT_LOGIN.getMsg());
    }

    /**
     * 获取当前登录用户的详细信息
     * */
    @RequestMapping(value = "get_information.do")
    public ServerResponse get_information(HttpSession session){
        Object o = session.getAttribute(Const.CURRENTUSER);
        if(o!=null&& o instanceof UserInfo){
            UserInfo userInfo = (UserInfo) o;
            return ServerResponse.createServerResponseBySuccess(null,userInfo);
        }
        return ServerResponse.createServerResponseByError(ResponseCode.USER_NOT_LOGIN.getStatus(),ResponseCode.USER_NOT_LOGIN.getMsg());
    }

}
