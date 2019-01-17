package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.common.TokenCache;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse register(UserInfo userInfo) {

//        步骤一：参数非空效验
        if(userInfo == null){
            return ServerResponse.createServerResponseByError(ResponseCode.PARAM_EMPTY.getStatus(),ResponseCode.PARAM_EMPTY.getMsg());
        }
//        步骤二：判断用户名是否已经存在
        String username = userInfo.getUsername();
//        int count = userInfoMapper.checkUsername(username);
//        用户名已存在
        /*if(count>0){
            return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_USERNAME.getStatus(),ResponseCode.EXISTS_USERNAME.getMsg());
        }*/
        ServerResponse serverResponse = check_valid(username,Const.USERNAME);
        if(!serverResponse.isSuccess()){
            return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_USERNAME.getStatus(),ResponseCode.EXISTS_USERNAME.getMsg());
        }
//        步骤三：判断邮箱是否已经存在
        /*int result = userInfoMapper.checkEmail(userInfo.getEmail());
//        邮箱已存在
        if(result>0){
            return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_EMAIL.getStatus(),ResponseCode.EXISTS_EMAIL.getMsg());
        }*/
        ServerResponse email_serverResponse = check_valid(userInfo.getEmail(),Const.EMAIL);
        if(!serverResponse.isSuccess()){
            return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_EMAIL.getStatus(),ResponseCode.EXISTS_EMAIL.getMsg());
        }
//        步骤四：注册
        userInfo.setRole(Const.USER_ROLE_CUSTOMER);
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int insert_rseult = userInfoMapper.insert(userInfo);
//        步骤五：返回数据
        if(insert_rseult>0) {
            return ServerResponse.createServerResponseBySuccess("注册成功");
        }
        return ServerResponse.createServerResponseByError("注册失败");
        /*int count = userInfoMapper.insert(userInfo);
        if(count>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError();*/
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
        /*int result = userInfoMapper.checkUsername(username);
        //用户名不存在
        if(result<=0){
            return ServerResponse.createServerResponseByError("用户名不存在");
        }*/
        ServerResponse serverResponse = check_valid(username,Const.USERNAME);
        if(serverResponse.isSuccess()){
            return ServerResponse.createServerResponseByError(ResponseCode.NOT_EXISTS_USERNAME.getStatus(),ResponseCode.NOT_EXISTS_USERNAME.getMsg());
        }
        //步骤三：根据用户名和密码查询
        UserInfo userInfo = userInfoMapper.selectUserByUsernameAndPassword(username,MD5Utils.getMD5Code(password));
        //密码错误
        if(userInfo == null){
            return ServerResponse.createServerResponseByError("密码错误");
        }
        //步骤四：处理结果并返回
        userInfo.setPassword("");
        return ServerResponse.createServerResponseBySuccess(null,userInfo);
    }

    @Override
    public ServerResponse check_valid(String str, String type) {
//        步骤一：参数的非空效验
        if(StringUtils.isBlank(str)||StringUtils.isBlank(type)){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
//        步骤二：判断用户名或者邮箱是否存在
        if(type.equals(Const.USERNAME)){
            int username_result = userInfoMapper.checkUsername(str);
            if(username_result>0){
                return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_USERNAME.getStatus(),ResponseCode.EXISTS_USERNAME.getMsg());
            }
            return ServerResponse.createServerResponseBySuccess("成功");
        }else if(type.equals(Const.EMAIL)){
            int email_result = userInfoMapper.checkEmail(str);
            if(email_result>0){
                return ServerResponse.createServerResponseByError(ResponseCode.EXISTS_EMAIL.getStatus(),ResponseCode.EXISTS_EMAIL.getMsg());
            }
            return ServerResponse.createServerResponseBySuccess("成功");
        }
//        步骤三：返回结果
        return ServerResponse.createServerResponseByError("type参数传递有误");
    }

    @Override
    public ServerResponse forget_get_question(String username) {
//        步骤一：参数的非空校验
        if(StringUtils.isBlank(username)){
            return ServerResponse.createServerResponseByError(ResponseCode.PARAM_EMPTY.getStatus(),ResponseCode.PARAM_EMPTY.getMsg());
        }
//        步骤二：判断用户是否存在
        ServerResponse serverResponse = check_valid(username,Const.USERNAME);
//        用户名不存在
        if(serverResponse.getStatus()!=ResponseCode.EXISTS_USERNAME.getStatus()){
            return ServerResponse.createServerResponseByError(ResponseCode.NOT_EXISTS_USERNAME.getStatus(),ResponseCode.NOT_EXISTS_USERNAME.getMsg());
        }
//        步骤三：查询密保问题
        String question = userInfoMapper.selectQuestionByUsername(username);
        if(StringUtils.isBlank(question)){
            return ServerResponse.createServerResponseByError("密保问题为空");
        }
//        步骤四：返回结果
        return ServerResponse.createServerResponseBySuccess(null,question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
//        步骤一：参数的非空校验
        if(StringUtils.isBlank(username)||StringUtils.isBlank(question)||StringUtils.isBlank(answer)){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
//        步骤二：校验答案
        int count = userInfoMapper.checkAnswerByUsernameAndQuestion(username,question,answer);
        if(count<=0){
            return ServerResponse.createServerResponseByError("答案错误");
        }
//        步骤三：返回用户的唯一标识
        String user_token = UUID.randomUUID().toString();
        TokenCache.put(username,user_token);

//        步骤四：返回结果
        return ServerResponse.createServerResponseBySuccess(null,user_token);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew,String forgetToken){
//        步骤一：参数非空效验
        if(StringUtils.isBlank(username)||StringUtils.isBlank(passwordNew)||StringUtils.isBlank(forgetToken)){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }

//        步骤二：校验token
                String token = TokenCache.get(username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createServerResponseByError("token不存在或者过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.createServerResponseByError("token不一致");
        }

//        步骤三：更新密码
        int count = userInfoMapper.updatePasswordByUsername(username,MD5Utils.getMD5Code(passwordNew));
        if(count<=0){
            return ServerResponse.createServerResponseByError("密码修改失败");
        }
//        步骤四：返回结果

        return ServerResponse.createServerResponseBySuccess();
    }

    @Override
    public ServerResponse reset_password(UserInfo userInfo, String passwordOld, String passwordNew){
//        步骤一：参数非空校验
        if(StringUtils.isBlank(passwordOld)||StringUtils.isBlank(passwordNew)){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
//        步骤二：校验旧密码是否正确(防止用户的横向越权)
        UserInfo userInfoOld = userInfoMapper.selectUserByUsernameAndPassword(userInfo.getUsername(),MD5Utils.getMD5Code(passwordOld));
        if(userInfoOld==null){
            return ServerResponse.createServerResponseByError("旧密码错误");
        }
//        步骤三：修改密码
        int count = userInfoMapper.updatePasswordByUsername(userInfo.getUsername(),MD5Utils.getMD5Code(passwordNew));
//        步骤四：返回结果
        if(count<=0){
            return ServerResponse.createServerResponseByError("密码修改失败");
        }
        return ServerResponse.createServerResponseBySuccess("密码修改成功");
    }

}
