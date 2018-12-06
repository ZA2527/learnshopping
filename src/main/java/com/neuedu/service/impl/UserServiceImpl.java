package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse register(UserInfo userInfo) {

//        步骤一：参数非空效验
        if(userInfo == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
//        步骤二：用户名效验
        int result = userInfoMapper.checkUsername(userInfo.getUsername());
        if(result >=0 ){
            return ServerResponse.createServerResponseByError("用户名已存在");
        }
//        步骤三：效验邮箱

        /*int count = userInfoMapper.insert(userInfo);
        if(count>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError();*/
        return null;
    }

    @Override
    public ServerResponse login(String username, String password) {
        //步骤一：参数非空效验
        if(StringUtils.isBlank(username)){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if(StringUtils.isBlank(password)){
            return ServerResponse.createServerResponseByError("密码不能为空");
        }
        //步骤二：检查用户名是否存在
        int result = userInfoMapper.checkUsername(username);
        //用户名不存在
        if(result<=0){
            return ServerResponse.createServerResponseByError("用户名不存在");
        }
        //步骤三：根据用户名和密码查询
        UserInfo userInfo = userInfoMapper.selectUserByUsernameAndPassword(username,password);
        //密码错误
        if(userInfo == null){
            return ServerResponse.createServerResponseByError("密码错误");
        }
        //步骤四：处理结果并返回
        userInfo.setPassword("");
        return ServerResponse.createServerResponseBySuccess(null,userInfo);
    }
}
