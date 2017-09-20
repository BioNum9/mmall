package com.mmall.util;

import com.mmall.pojo.User;

/**
 * Created by lovea on 2017/7/16.
 */
public class CommonUtil {

    /**
     * 查看是否登陆
     * @param user
     * @return
     */
    public static boolean checkIsLogin(User user){
        if(user == null){
            return false;
        }else{
            return true;
        }
    }

}
