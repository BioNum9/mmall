package com.mmall.controller.backend;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by lovea on 2017/7/16.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 添加商品分类
     * @param session
     * @param categoryName 分类名称
     * @param parentID 父ID 默认为0
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentID" , defaultValue = "0") int parentID){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }

        //校验下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //增加分类
            return iCategoryService.addCategory(categoryName,parentID);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }

    }

    /**
     * 更新商品名称
     * @param session
     * @param categoryID
     * @param categoryName
     * @return
     */
    @RequestMapping("update_categoryName.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session,Integer categoryID,String categoryName){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //更新名字
            return iCategoryService.updateCategoryName(categoryID,categoryName);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }
    }

    /**
     *获取商品子类型列表
     * @param session
     * @param categoryID
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCaterogy(HttpSession session,@RequestParam(value = "categoryID",defaultValue = "0") Integer categoryID){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，不递归，保持平级
            return iCategoryService.getChildrenParallelCaterogy(categoryID);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }
    }

    /**
     * 根据当前ID，递归查询所有子节点id
     * @param session
     * @param categoryID
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryID",defaultValue = "0") Integer categoryID){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点id和递归子节点的ID
            return iCategoryService.selectCategoryAndChildrenByID(categoryID);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }
    }

//    private static ServerResponse checkIsLogin(User user){
//        if(user == null){
//            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
//        }
//        return ServerResponse.createBySuccess();
//    }
}
