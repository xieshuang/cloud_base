package com.cloudbase.system.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.cloudbase.common.core.constant.Constants;
import com.cloudbase.common.log.entity.SysLog;
import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.entity.LoginUser;
import com.cloudbase.common.security.service.JwtService;
import com.cloudbase.system.entity.SysSocialUser;
import com.cloudbase.system.entity.SysUser;
import com.cloudbase.system.service.ISysLogService;
import com.cloudbase.system.service.ISysMenuService;
import com.cloudbase.system.service.ISysSocialUserService;
import com.cloudbase.system.service.ISysUserService;
import com.cloudbase.system.service.IWeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ISysUserService userService;
    private final ISysMenuService menuService;
    private final ISysLogService logService;
    private final ISysSocialUserService socialUserService;
    private final IWeixinService weixinService;
    private final JwtService jwtService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginBody, HttpServletRequest request) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");

        SysLog loginLog = new SysLog();
        loginLog.setUsername(username);
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/login");

        try {
            SysUser user = userService.selectByUsername(username);
            if (user == null) {
                loginLog.setStatus(1);
                loginLog.setErrorMsg("用户名不存在");
                logService.saveLog(loginLog);
                return Result.error(401, "用户名或密码错误");
            }

            if (user.getStatus() != 1) {
                loginLog.setStatus(1);
                loginLog.setErrorMsg("账号已被禁用");
                logService.saveLog(loginLog);
                return Result.error(401, "账号已被禁用");
            }

            if (!userService.verifyPassword(password, user.getPassword())) {
                loginLog.setStatus(1);
                loginLog.setErrorMsg("密码错误");
                logService.saveLog(loginLog);
                return Result.error(401, "用户名或密码错误");
            }

            Set<String> roles = Set.of("admin");
            Set<String> permissions = new java.util.HashSet<>(menuService.selectPermsByUserId(user.getId()));

            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(user.getId());
            loginUser.setUsername(user.getUsername());
            loginUser.setRoles(roles);
            loginUser.setPermissions(permissions);

            String token = jwtService.generateToken(user.getId(), user.getUsername());
            loginUser.setToken(token);

            loginLog.setStatus(0);
            loginLog.setUserId(user.getId());
            logService.saveLog(loginLog);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", loginUser);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            loginLog.setStatus(1);
            loginLog.setErrorMsg(e.getMessage());
            logService.saveLog(loginLog);
            throw e;
        }
    }

    @ApiOperation("获取用户信息")
    @PostMapping("/getUserInfo")
    public Result<LoginUser> getUserInfo() {
        return Result.success(new LoginUser());
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    @OperLog(module = "认证模块", content = "退出登录", logType = 0)
    public Result<?> logout() {
        return Result.success("退出成功");
    }

    @ApiOperation("微信小程序登录")
    @PostMapping("/weixin/mini/login")
    public Result<Map<String, Object>> weixinMiniLogin(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }

        Map<String, String> sessionData = weixinService.miniProgramLogin(code);
        String openid = sessionData.get("openid");
        String unionid = sessionData.get("unionid");

        SysLog loginLog = new SysLog();
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mini/login");

        SysSocialUser socialUser = socialUserService.findByOpenid(SysSocialUser.SOCIAL_TYPE_WEI_XIN_MINI, openid);
        if (socialUser != null && socialUser.getUserId() != null) {
            SysUser user = userService.selectById(socialUser.getUserId());
            if (user == null) {
                return Result.error("绑定的用户不存在");
            }
            return buildLoginResult(user, loginLog, "微信小程序登录成功");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("needBind", true);
        data.put("openid", openid);
        data.put("unionid", unionid);
        data.put("socialType", SysSocialUser.SOCIAL_TYPE_WEI_XIN_MINI);
        return Result.success("需要绑定账号", data);
    }

    @ApiOperation("微信小程序绑定已有账号")
    @PostMapping("/weixin/mini/bind")
    public Result<Map<String, Object>> weixinMiniBind(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String openid = params.get("openid");
        String username = params.get("username");
        String password = params.get("password");
        String unionid = params.get("unionid");

        SysUser user = userService.selectByUsername(username);
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            return Result.error(401, "账号已被禁用");
        }
        if (!userService.verifyPassword(password, user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        socialUserService.bindUser(user.getId(), SysSocialUser.SOCIAL_TYPE_WEI_XIN_MINI, openid, unionid, null, null);

        SysLog loginLog = new SysLog();
        loginLog.setUsername(username);
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mini/bind");
        loginLog.setStatus(0);
        loginLog.setUserId(user.getId());
        logService.saveLog(loginLog);

        return buildLoginResult(user, loginLog, "绑定并登录成功");
    }

    @ApiOperation("微信小程序创建新账号并绑定")
    @PostMapping("/weixin/mini/create")
    public Result<Map<String, Object>> weixinMiniCreate(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String openid = params.get("openid");
        String unionid = params.get("unionid");
        String nickname = params.get("nickname");

        SysUser user = new SysUser();
        user.setUsername("wxmini_" + openid.substring(0, 16));
        user.setPassword(userService.encodePassword(openid.substring(0, 8)));
        user.setNickname(nickname != null ? nickname : "微信用户");
        user.setStatus(1);
        userService.save(user);

        socialUserService.bindUser(user.getId(), SysSocialUser.SOCIAL_TYPE_WEI_XIN_MINI, openid, unionid, nickname, null);

        SysLog loginLog = new SysLog();
        loginLog.setUsername(user.getUsername());
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mini/create");
        loginLog.setStatus(0);
        loginLog.setUserId(user.getId());
        logService.saveLog(loginLog);

        return buildLoginResult(user, loginLog, "创建账号并绑定成功");
    }

    @ApiOperation("微信公众号登录")
    @PostMapping("/weixin/mp/login")
    public Result<Map<String, Object>> weixinMpLogin(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }

        Map<String, String> sessionData = weixinService.mpGetOpenId(code);
        String openid = sessionData.get("openid");
        String unionid = sessionData.get("unionid");

        SysLog loginLog = new SysLog();
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mp/login");

        SysSocialUser socialUser = socialUserService.findByOpenid(SysSocialUser.SOCIAL_TYPE_WEI_XIN_MP, openid);
        if (socialUser != null && socialUser.getUserId() != null) {
            SysUser user = userService.selectById(socialUser.getUserId());
            if (user == null) {
                return Result.error("绑定的用户不存在");
            }
            return buildLoginResult(user, loginLog, "微信公众号登录成功");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("needBind", true);
        data.put("openid", openid);
        data.put("unionid", unionid);
        data.put("socialType", SysSocialUser.SOCIAL_TYPE_WEI_XIN_MP);
        return Result.success("需要绑定账号", data);
    }

    @ApiOperation("微信公众号绑定已有账号")
    @PostMapping("/weixin/mp/bind")
    public Result<Map<String, Object>> weixinMpBind(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String openid = params.get("openid");
        String username = params.get("username");
        String password = params.get("password");
        String unionid = params.get("unionid");

        SysUser user = userService.selectByUsername(username);
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            return Result.error(401, "账号已被禁用");
        }
        if (!userService.verifyPassword(password, user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        socialUserService.bindUser(user.getId(), SysSocialUser.SOCIAL_TYPE_WEI_XIN_MP, openid, unionid, null, null);

        SysLog loginLog = new SysLog();
        loginLog.setUsername(username);
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mp/bind");
        loginLog.setStatus(0);
        loginLog.setUserId(user.getId());
        logService.saveLog(loginLog);

        return buildLoginResult(user, loginLog, "绑定并登录成功");
    }

    @ApiOperation("微信公众号创建新账号并绑定")
    @PostMapping("/weixin/mp/create")
    public Result<Map<String, Object>> weixinMpCreate(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String openid = params.get("openid");
        String unionid = params.get("unionid");
        String nickname = params.get("nickname");

        SysUser user = new SysUser();
        user.setUsername("wxmp_" + openid.substring(0, 16));
        user.setPassword(userService.encodePassword(openid.substring(0, 8)));
        user.setNickname(nickname != null ? nickname : "微信用户");
        user.setStatus(1);
        userService.save(user);

        socialUserService.bindUser(user.getId(), SysSocialUser.SOCIAL_TYPE_WEI_XIN_MP, openid, unionid, nickname, null);

        SysLog loginLog = new SysLog();
        loginLog.setUsername(user.getUsername());
        loginLog.setLogType(Constants.LogType.LOGIN);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setIp(ServletUtil.getClientIP(request));
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setRequestUrl("/auth/weixin/mp/create");
        loginLog.setStatus(0);
        loginLog.setUserId(user.getId());
        logService.saveLog(loginLog);

        return buildLoginResult(user, loginLog, "创建账号并绑定成功");
    }

    @ApiOperation("获取第三方绑定状态")
    @GetMapping("/social/bind/status")
    public Result<Map<String, Object>> getSocialBindStatus(@RequestParam Long userId) {
        Map<String, Object> data = new HashMap<>();
        SysSocialUser miniBind = socialUserService.findByUserId(userId);
        data.put("miniBind", miniBind != null);
        data.put("mpBind", miniBind != null);
        return Result.success(data);
    }

    private Result<Map<String, Object>> buildLoginResult(SysUser user, SysLog loginLog, String message) {
        Set<String> roles = Set.of("admin");
        Set<String> permissions = new java.util.HashSet<>(menuService.selectPermsByUserId(user.getId()));

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        loginUser.setToken(token);

        loginLog.setStatus(0);
        loginLog.setUserId(user.getId());
        logService.saveLog(loginLog);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", loginUser);

        return Result.success(message, data);
    }
}
