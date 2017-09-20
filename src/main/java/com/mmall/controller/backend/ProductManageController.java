package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CommonUtil;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by lovea on 2017/7/16.
 */
@RequestMapping("/manage/product")
@Controller
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    /**
     * 保存或更新商品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session ,Product product){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    /**
     * 更新产品销售状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("update_sale_status.do")
    @ResponseBody
    public ServerResponse updateSaleStatus(HttpSession session ,Integer productId,Integer status){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.updateSaleStatus( productId, status);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    /**
     * 获取商品信息详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session ,Integer productId){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageGetProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }


    /**
     * 获取商品信息列表
     * @param session
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session , @RequestParam(value = "pageNumber",defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageGetProductList(pageNumber,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    /**
     * 搜索商品信息
     * @param session
     * @param productName
     * @param productId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session ,String productName,Integer productId, @RequestParam(value = "pageNumber",defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNumber,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    /**
     * 上传文件
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file" ,required = false) MultipartFile file, HttpServletRequest request){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"请登录后进行操作");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    /**
     * 富文本上传
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file" ,required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap = Maps.newHashMap();

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if(!CommonUtil.checkIsLogin(user)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录后操作");
            return resultMap;
        }

        //富文本中对返回格式有要求
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }





}
