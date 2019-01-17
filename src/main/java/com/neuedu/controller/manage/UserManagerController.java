package com.neuedu.controller.manage;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/user/")
public class UserManagerController{

    @Autowired
    IUserService userService;

//    管理员登录
@RequestMapping(value = "login.do")
public ServerResponse login(HttpSession session,String username, String password){
    ServerResponse serverResponse = userService.login(username,password);
    if(serverResponse.isSuccess()){
//           保存登录状态
           /*Const.CURRENTUSER 代表用户表中的字段名
           serverResponse.getData() 代表字段中所对应的值*/

//           判断是否是管理员角色
        UserInfo userInfo = (UserInfo) serverResponse.getData();
        if(userInfo.getRole()==Const.USER_ROLE_CUSTOMER){
            return ServerResponse.createServerResponseByError("无权限登录");
        }
        session.setAttribute(Const.CURRENTUSER,userInfo);
    }
    return serverResponse;
}

}
