package com.mmall.service.impl;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by lovea on 2017/7/6.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;



    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);

        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("用户名不存在");
        }

        //密码登陆MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);

        if(user == null){
            return ServerResponse.createByErrorMsg("密码错误");
        }
        //set password empty
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){
        //check username
        ServerResponse validResponse = this.checkValid(user.getUsername(),Constant.USERNAME);

        if(!validResponse.isSuccess()) {
            return validResponse;
        }
        //check email
        validResponse = this.checkValid(user.getEmail(),Constant.EMAIL);
        if(!validResponse.isSuccess()){
            return  validResponse;
        }

        user.setRole(Constant.Role.ROLE_CUSTOMER);
        //MD5 ENCODE
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);

        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("注册失败");
        }

        return ServerResponse.createBySuccessMsg("注册成功");
    }

    /**
     * 检查字段有效性
     * @param str 字段值
     * @param type 字段类型
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNoneBlank(type)){
            //开始校验
            if(Constant.USERNAME.equals(type)){
                //check username
                int resultCount = userMapper.checkUsername(str);

                if(resultCount > 0){
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }

            if(Constant.EMAIL.equals(type)){
                //check email
                int resultCount = userMapper.checkEmail(str);

                if(resultCount > 0){
                    return ServerResponse.createByErrorMsg("Email已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMsg("参数错误");
        }

        return ServerResponse.createBySuccessMsg("校验成功");
    }

    /**
     * 查找用户修改密码问题
     * @param username 用户名
     * @return
     */
    public ServerResponse<String> selectQuestion(String username) {
        //check username is valid
        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);

        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMsg("无找回密码问题");
    }

    /**
     * 验证用户修改密码问题是否正确
     * @param username 用户名
     * @param question 问题
     * @param answer 回答
     * @return
     */
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        //check answer
        int resultCount = userMapper.checkAnswer(username, question, answer);

        if(resultCount > 0 ){
            //说明问题回答正确
            String forgetToken = UUID.randomUUID().toString();
            //设置token
            TokenCache.setKey(Constant.TOKEN_HEAD+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }

        return ServerResponse.createByErrorMsg("问题答案错误");
    }

    /**
     * 忘记密码重置
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    public ServerResponse<String>  forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("参数错误，token需要传递");
        }
        //check username is valid
        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        String token = TokenCache.getKey(Constant.TOKEN_HEAD+username);

        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token失效");
        }

        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMsg("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMsg("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    /**
     * 重置用户密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));

        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMsg("密码更新成功");
        }

        return ServerResponse.createByErrorMsg("密码更新失败");
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    public ServerResponse<User> updateUserInfo(User user){
        //username不能更新
        //email进行校验，校验新的email是不是已经存在，并且存在的email如果相同，是不是当前用户的
        int resultCount = userMapper.checkEmailByUserID(user.getEmail(),user.getId());
        if(resultCount >0 ){
            return ServerResponse.createByErrorMsg("email已存在，请重新输入");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);

        updateUser.setUsername(user.getUsername());
        if(resultCount > 0){
            return ServerResponse.createBySuccess("更新信息成功",updateUser);
        }
        return  ServerResponse.createByErrorMsg("更新信息失败");

    }

    /**
     * 查询用户信息
     * @param userID
     * @return
     */
    public ServerResponse<User> getUserInfo(Integer userID){
        User user = userMapper.selectByPrimaryKey(userID);
        if(user == null){
            return ServerResponse.createByErrorMsg("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //==============================backend================================

    /**
     * 判断当前用户是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Constant.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }

}
