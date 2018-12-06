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

}
