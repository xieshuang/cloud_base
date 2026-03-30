package com.cloudbase.system.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.cloudbase.common.core.constant.Constants;
import com.cloudbase.common.log.entity.SysLog;
import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.entity.LoginUser;
import com.cloudbase.common.security.service.JwtService;
import com.cloudbase.system.entity.SysUser;
import com.cloudbase.system.service.ISysLogService;
import com.cloudbase.system.service.ISysMenuService;
import com.cloudbase.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
