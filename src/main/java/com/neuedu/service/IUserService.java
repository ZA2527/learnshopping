package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;

public interface IUserService{

    /***
     * 注册接口
     * @param userInfo
     * @return
     */
    public ServerResponse register(UserInfo userInfo);

    /***
     * 登录
     */
    public ServerResponse login(String username,String password);

    /**
     * 检测用户名或邮箱是否有效
     * */
    public ServerResponse check_valid(String str,String type);

    }
