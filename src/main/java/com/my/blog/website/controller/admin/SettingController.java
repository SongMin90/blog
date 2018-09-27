package com.my.blog.website.controller.admin;

import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.BackResponseBo;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.OptionVo;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IOptionService;
import com.my.blog.website.utils.GsonUtils;
import com.my.blog.website.utils.TaleUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangq on 2017/3/20.
 */
@Controller
@RequestMapping("/admin/setting")
public class SettingController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

    @Resource
    private IOptionService optionService;

    @Resource
    private ILogService logService;

    @Resource
    private ISiteService siteService;

    /**
     * 系统设置
     */
    @GetMapping(value = "")
    public String setting(HttpServletRequest request) {
        List<OptionVo> voList = optionService.getOptions();
        Map<String, String> options = new HashMap<>();
        voList.forEach((option) -> {
            options.put(option.getName(), option.getValue());
        });
        request.setAttribute("options", options);
        return "admin/setting";
    }

    /**
     * 保存系统设置
     */
    @PostMapping(value = "")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveSetting(@RequestParam(required = false) String site_theme, HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> querys = new HashMap<>();
            parameterMap.forEach((key, value) -> {
                querys.put(key, join(value));
            });

            optionService.saveOptions(querys);

            WebConst.initConfig = querys;

            if (StringUtils.isNotBlank(site_theme)) {
                BaseController.THEME = "themes/" + site_theme;
            }
            logService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJsonString(querys), request.getRemoteAddr(), this.getUid(request));
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "保存设置失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }


    /**
     * 系统备份
     *
     * @return
     */
    @PostMapping(value = "backup")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo backup(@RequestParam String bk_type, @RequestParam String bk_path,
                                 HttpServletRequest request) {
        if (StringUtils.isBlank(bk_type)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }
        try {
            BackResponseBo backResponse = siteService.backup(bk_type, bk_path, "yyyyMMddHHmm");
            logService.insertLog(LogActions.SYS_BACKUP.getAction(), null, request.getRemoteAddr(), this.getUid(request));
            return RestResponseBo.ok(backResponse);
        } catch (Exception e) {
            String msg = "备份失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }

    /**
     * 更换网站图标
     * 
     * @param request
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping(value = "updateFavicon")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo updateFavicon(HttpServletRequest request, @RequestParam("faviconFile") MultipartFile multipartFile) {
        String msg = "上传出错！";
        try {
            if (multipartFile.getSize() <= WebConst.MAX_FILE_SIZE) {
                File file = new File(TaleUtils.getFaviconPath());
                try {
                    FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                    return RestResponseBo.ok();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                msg = "上传文件大于1M";
            }
        } catch (Exception e) {
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
        }
        return RestResponseBo.fail(msg);
    }

    /**
     * 升级
     * @param webFileURL
     * @return
     */
    @PostMapping(value = "webUpdate")
    @ResponseBody
    public RestResponseBo webUpdate(@RequestParam("webFileURL") String webFileURL) {
        String[] cmds = {"/bin/sh", "-c", "/usr/blog/restart.sh " + webFileURL};
        try {
            /*URL url = new URL(webFileURL);
            url.openStream();*/
            Runtime.getRuntime().exec(cmds);
            return RestResponseBo.ok();
        } catch (Exception e) {
            return RestResponseBo.fail("项目地址有误！");
        }
    }

    /**
     * 数组转字符串
     *
     * @param arr
     * @return
     */
    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        String[] var3 = arr;
        int var4 = arr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String item = var3[var5];
            ret.append(',').append(item);
        }
        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }

}
