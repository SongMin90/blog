package com.my.blog.website.interceptor;

import com.my.blog.website.dao.VisitorVoMapper;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.service.VisitorService;
import com.my.blog.website.utils.*;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.Types;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *
 * @author SongM
 * @date 2017/3/9
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
//    private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
//    private static final String USER_AGENT = "user-agent";

    @Resource
    private IUserService userService;

    private MapCache cache = MapCache.single();

    @Resource
    private Commons commons;

    @Resource
    private AdminCommons adminCommons;
    
    @Resource
    private VisitorService visitorService;

    @Resource
    private VisitorVoMapper visitorDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();

        // robots.txt
        if(uri.startsWith("/robots.txt")) {
            request.getRequestDispatcher(request.getContextPath() + "/user/robots.txt").forward(request, response);
            return false;
        }

        // 拦截IP黑名单
        if(uri.startsWith("/admin/blacklist")) {
            return true;
        }
        String ip = TerminalUtil.getIp(request);
        String terminalInfo = TerminalUtil.getTerminalInfo(request);
        String req_id = TaleUtils.MD5encode(ip + terminalInfo);
        // 从缓存取
        Long isIntercept = MapCache.single().get(req_id);
        if (isIntercept == null) {
            // 读数据库
            isIntercept = visitorDao.idIsIntercept(req_id);
            // 存7天
            MapCache.single().set(req_id, isIntercept,3600 * 24 * 7);
        }
        // 判断是否拦截
        if(isIntercept > 0) {
            request.getRequestDispatcher(request.getContextPath() + "/admin/blacklist").forward(request, response);
            return false;
        }

        // 记录访客信息
        visitorService.save(request, req_id, ip, terminalInfo);

        // cookie添加userId
        Commons.setCookie("userId", req_id,1, response);

        // 请求拦截处理
        UserVo user = TaleUtils.getLoginUser(request);
        if (null == user) {
            Integer uid = TaleUtils.getCookieUid(request);
            if (null != uid) {
                // 记住登录，这里还是有安全隐患，cookie是可以伪造的
                user = userService.queryUserById(uid);
                request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
            }
        }
        if (uri.startsWith("/admin") && !uri.startsWith("/admin/login") && null == user) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }

        // 设置get请求的token
        /*if (request.getMethod().equals("GET")) {
            String csrf_token = UUID.UU64();
            // 默认存储30分钟
            cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, uri, 30 * 60);
            request.setAttribute("_csrf_token", csrf_token);
        }*/

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
        // 一些工具类和公共方法
        httpServletRequest.setAttribute("commons", commons);
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
