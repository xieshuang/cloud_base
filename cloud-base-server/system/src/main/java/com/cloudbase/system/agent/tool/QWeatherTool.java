package com.cloudbase.system.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 和风天气查询工具类
 * <p>
 * 通过 AgentScope 的 @Tool 注解将方法暴露给 Agent 调用，
 * Agent 会根据用户意图自动决定是否调用这些工具获取天气数据。
 */
@Slf4j
@Component
public class QWeatherTool {

    /** 和风天气 API Key，从配置文件中注入 */
    @Value("${qweather.api-key}")
    private String apiKey;

    /** 和风天气开发服务基础地址 */
    @Value("${qweather.base-url:https://devapi.qweather.com/v7}")
    private String baseUrl;

    /** 和风天气地理位置查询 API 地址 */
    private static final String GEO_API_URL = "you_domain/geo/v2/city/lookup";

    /**
     * 获取指定城市的实时天气信息
     *
     * @param city 城市名称，如"北京"、"上海"
     * @return 格式化的实时天气信息文本
     */
    @Tool(name = "get_current_weather", description = "获取指定城市的实时天气信息，包括温度、天气状况、湿度、风力等")
    public String getCurrentWeather(
            @ToolParam(name = "city", description = "城市名称，如'北京'、'上海'、'深圳'") String city) {
        log.info("查询实时天气：city={}", city);

        // 第一步：通过城市名称查询 Location ID
        String locationId = getLocationId(city);
        if (locationId == null) {
            return String.format("抱歉，未能找到城市「%s」的天气信息，请检查城市名称是否正确。", city);
        }

        // 第二步：通过 Location ID 查询实时天气
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("location", locationId);
            params.put("key", apiKey);

            String url = baseUrl + "/weather/now";
            String response = HttpUtil.get(url, params);
            log.debug("和风天气实时天气响应：{}", response);

            JSONObject json = JSON.parseObject(response);
            String code = json.getString("code");

            if (!"200".equals(code)) {
                log.warn("和风天气 API 返回异常：code={}", code);
                return String.format("查询城市「%s」的天气时出现异常，请稍后再试。", city);
            }

            JSONObject now = json.getJSONObject("now");
            if (now == null) {
                return String.format("城市「%s」暂无实时天气数据。", city);
            }

            return formatCurrentWeather(city, now);

        } catch (Exception e) {
            log.error("调用和风天气 API 失败", e);
            return String.format("查询城市「%s」的天气时出现网络异常，请稍后再试。", city);
        }
    }

    /**
     * 获取指定城市未来几天的天气预报
     *
     * @param city 城市名称
     * @param days 预报天数（默认3天，最多7天）
     * @return 格式化的天气预报文本
     */
    @Tool(name = "get_weather_forecast", description = "获取指定城市未来几天的天气预报，包括每日天气、温度范围等")
    public String getWeatherForecast(
            @ToolParam(name = "city", description = "城市名称，如'北京'、'上海'") String city,
            @ToolParam(name = "days", description = "预报天数，1-7之间的整数，默认3天") Integer days) {
        log.info("查询天气预报：city={}, days={}", city, days);

        // 参数校验
        int forecastDays = (days == null || days < 1) ? 3 : Math.min(days, 7);

        // 通过城市名称查询 Location ID
        String locationId = getLocationId(city);
        if (locationId == null) {
            return String.format("抱歉，未能找到城市「%s」的天气信息，请检查城市名称是否正确。", city);
        }

        // 通过 Location ID 查询天气预报
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("location", locationId);
            params.put("key", apiKey);

            String url = baseUrl + "/weather/" + (forecastDays <= 3 ? "3d" : "7d");
            String response = HttpUtil.get(url, params);
            log.debug("和风天气预报响应：{}", response);

            JSONObject json = JSON.parseObject(response);
            String code = json.getString("code");

            if (!"200".equals(code)) {
                log.warn("和风天气 API 返回异常：code={}", code);
                return String.format("查询城市「%s」的天气预报时出现异常，请稍后再试。", city);
            }

            JSONArray daily = json.getJSONArray("daily");
            if (daily == null || daily.isEmpty()) {
                return String.format("城市「%s」暂无天气预报数据。", city);
            }

            return formatWeatherForecast(city, daily, forecastDays);

        } catch (Exception e) {
            log.error("调用和风天气 API 失败", e);
            return String.format("查询城市「%s」的天气预报时出现网络异常，请稍后再试。", city);
        }
    }

    /**
     * 通过城市名称获取和风天气 Location ID
     */
    private String getLocationId(String city) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("location", city);
            params.put("key", apiKey);

            String response = HttpUtil.get(GEO_API_URL, params);
            log.debug("地理位置查询响应：{}", response);

            JSONObject json = JSON.parseObject(response);
            String code = json.getString("code");

            if (!"200".equals(code)) {
                return null;
            }

            JSONArray locationList = json.getJSONArray("location");
            if (locationList == null || locationList.isEmpty()) {
                return null;
            }

            // 取第一个匹配结果
            JSONObject location = locationList.getJSONObject(0);
            return location.getString("id");

        } catch (Exception e) {
            log.error("查询地理位置失败", e);
            return null;
        }
    }

    /**
     * 格式化实时天气信息为友好的文本
     */
    private String formatCurrentWeather(String city, JSONObject now) {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(city).append("实时天气】\n");
        sb.append("天气状况：").append(now.getString("text")).append("\n");
        sb.append("当前温度：").append(now.getString("temp")).append("℃\n");
        sb.append("体感温度：").append(now.getString("feelsLike")).append("℃\n");
        sb.append("相对湿度：").append(now.getString("humidity")).append("%\n");
        sb.append("风向风力：").append(now.getString("windDir"))
                .append(" ").append(now.getString("windScale")).append("级\n");
        sb.append("能见度：").append(now.getString("vis")).append("km\n");
        sb.append("气压：").append(now.getString("pressure")).append("hPa\n");

        String precip = now.getString("precip");
        if (StrUtil.isNotBlank(precip) && !"0.0".equals(precip)) {
            sb.append("降水量：").append(precip).append("mm\n");
        }

        sb.append("\n数据更新时间：").append(now.getString("obsTime"));
        return sb.toString();
    }

    /**
     * 格式化天气预报为友好的文本
     */
    private String formatWeatherForecast(String city, JSONArray daily, int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(city).append("未来").append(Math.min(days, daily.size())).append("天天气预报】\n\n");

        int count = Math.min(days, daily.size());
        for (int i = 0; i < count; i++) {
            JSONObject day = daily.getJSONObject(i);
            sb.append("📅 ").append(day.getString("fxDate")).append("\n");
            sb.append("   天气：").append(day.getString("textDay")).append("\n");
            sb.append("   温度：").append(day.getString("tempMin")).append("℃ ~ ")
                    .append(day.getString("tempMax")).append("℃\n");
            sb.append("   风向：").append(day.getString("windDirDay"))
                    .append(" ").append(day.getString("windScaleDay")).append("级\n");

            String precip = day.getString("precip");
            if (StrUtil.isNotBlank(precip) && !"0.0".equals(precip)) {
                sb.append("   降水量：").append(precip).append("mm\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
