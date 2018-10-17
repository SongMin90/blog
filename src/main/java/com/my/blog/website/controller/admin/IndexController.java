package com.my.blog.website.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.modal.Vo.*;
import com.my.blog.website.service.*;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Bo.StatisticsBo;
import com.my.blog.website.utils.GsonUtils;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台管理首页
 * Created by Administrator on 2017/3/9 009.
 */
@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private ISiteService siteService;

    @Resource
    private ILogService logService;

    @Resource
    private IUserService userService;
    
    @Resource
    private VisitorService visitorService;

    @Resource
    private ChatService chatService;

    /**
     * 页面跳转
     * @return
     */
    @GetMapping(value = {"","/index"})
    public String index(HttpServletRequest request){
        LOGGER.info("Enter admin index method");
        List<CommentVo> comments = siteService.recentComments(5);
        List<ContentVo> contents = siteService.recentContents(5);
        StatisticsBo statistics = siteService.getStatistics();
        // 取最新的5条日志
        List<LogVo> logs = logService.getLogs(1, 5);

        request.setAttribute("comments", comments);
        request.setAttribute("articles", contents);
        request.setAttribute("statistics", statistics);
        request.setAttribute("logs", logs);
        LOGGER.info("Exit admin index method");
        return "admin/index";
    }

    /**
     * 个人设置页面
     */
    @GetMapping(value = "profile")
    public String profile() {
        return "admin/profile";
    }

    /**
     * admin 退出登录
     * @return
     */
    @GetMapping(value = "logout")
    public String logout() {
        System.out.println("index-----------logout");
        return "admin/login";
    }


    /**
     * 保存个人信息
     */
    @PostMapping(value = "/profile")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveProfile(@RequestParam String screenName, @RequestParam String email, HttpServletRequest request, HttpSession session) {

        UserVo users = this.user(request);
        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            UserVo temp = new UserVo();
            temp.setUid(users.getUid());
            temp.setScreenName(screenName);
            temp.setEmail(email);
            userService.updateByUid(temp);
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJsonString(temp), request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            UserVo original = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            original.setScreenName(screenName);
            original.setEmail(email);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, original);
        }
        return RestResponseBo.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/password")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo upPwd(@RequestParam String oldPassword, @RequestParam String password, HttpServletRequest request,HttpSession session) {
        UserVo users = this.user(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }

        if (!users.getPassword().equals(TaleUtils.MD5encode(users.getUsername() + oldPassword))) {
            return RestResponseBo.fail("旧密码错误");
        }
        if (password.length() < 6 || password.length() > 14) {
            return RestResponseBo.fail("请输入6-14位密码");
        }

        try {
            UserVo temp = new UserVo();
            temp.setUid(users.getUid());
            String pwd = TaleUtils.MD5encode(users.getUsername() + password);
            temp.setPassword(pwd);
            userService.updateByUid(temp);
            logService.insertLog(LogActions.UP_PWD.getAction(), null, request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            UserVo original= (UserVo)session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            original.setPassword(pwd);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY,original);
            return RestResponseBo.ok();
        } catch (Exception e){
            String msg = "密码修改失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }
    
    /**
     * 分页查询所有访客信息
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "/visitors")
    public String visitors(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        PageInfo<VisitorVo> visitorsPaginator = visitorService.getVisitorsWithpage(page, limit);
        request.setAttribute("visitors", visitorsPaginator);
    	return "admin/visitor_list";
    }

    /**
     * 统计时间段访客
     * @param type
     * @return
     */
    @GetMapping(value = "/visitors/statistics")
    @ResponseBody
    public RestResponseBo<String> visitorsStatistics(@RequestParam(value = "type", required = true) int type) {
        JSONObject json = visitorService.visitorsStatistics(type);
        return RestResponseBo.ok(json);
    }

    /**
     * 拦截IP
     * @param id
     * @param request
     * @return
     */
    @PostMapping(value = "/interceptVisitor")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo interceptVisitor(@RequestParam String id, HttpServletRequest request) {
        try {
            // 对ip进行拦截
            VisitorVo visitorVo = visitorService.interceptById(id);
            // log
            if(visitorVo.isIntercept()) {
                logService.insertLog(LogActions.INTERCEPT_VISITOR.getAction(), visitorVo.getIp(), request.getRemoteAddr(), this.getUid(request));
            } else {
                logService.insertLog(LogActions.RELEASE_VISITOR.getAction(), visitorVo.getIp(), request.getRemoteAddr(), this.getUid(request));
            }
        } catch (Exception e) {
            String msg = "操作失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 分页查询所有日志
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "/logs")
    public String logs(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        PageInfo<LogVo> logsPaginator = logService.getLogsWithpage(page, limit);
        request.setAttribute("logs", logsPaginator);
        return "admin/log_list";
    }

    /**
     * 分页查询所有聊天记录
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "/chats")
    public String chats(@RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        PageInfo<ChatVo> chatsPaginator = chatService.getChatsWithpage(page, limit);
        request.setAttribute("chats", chatsPaginator);
        return "admin/chat_list";
    }

    /**
     * 清空所有聊天记录
     * @return
     */
    @PostMapping(value = "/cleanAllChats")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo cleanAllChats() {
        try {
            chatService.deleteAll();
        } catch (Exception e) {
            String msg = "操作失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

//    /**
//     * 获取系统信息
//     * @param type
//     * @return
//     */
//    @GetMapping(value = "/system/{type}")
//    @ResponseBody
//    public RestResponseBo systemInfo(@PathVariable String type) {
//        Object body = new Object();
//        if (type.equals("serverinfo")) {
//            body = SystemInfo.SystemProperty();
//        } else if (type.equals("memory")) {
//            body = SystemInfo.memory(new Sigar());
//        } else if (type.equals("cpuInfos")) {
//            body = SystemInfo.cpuInfos(new Sigar());
//        } else if (type.equals("diskInfos")) {
//            body = SystemInfo.diskInfos(new Sigar());
//        } else if (type.equals("all")) {
//            body = new JSONObject();
//            ((JSONObject) body).put("serverinfo", SystemInfo.SystemProperty());
//            Sigar sigar = new Sigar();
//            ((JSONObject) body).put("memory", SystemInfo.memory(sigar));
//            ((JSONObject) body).put("cpuInfos", SystemInfo.cpuInfos(sigar));
//            ((JSONObject) body).put("diskInfos", SystemInfo.diskInfos(sigar));
//        }
//        return RestResponseBo.ok(body);
//    }

}
