package com.pandaai.wechat.service;

import com.pandaai.wechat.entity.vo.wechat.token.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@CacheConfig(cacheNames = AccessTokenService.CACHE_NAME)
@Slf4j
public class AccessTokenService {
    protected static final String CACHE_NAME = "AccessTokenService";

    @Value("${wx.url.host}")
    private String serverHost;

    @Value("${wx.url.token.stable}")
    private String stableTokenUrl;

    @Value("${wx_jszaz_app_id}")
    private String appId;

    @Value("${wx_jszaz_app_secret}")
    private String appSec;

    @Cacheable(cacheManager = "expire2hManager", unless = "#result == null")
    public String obtainAccessToken() {
        String url = serverHost + stableTokenUrl;

        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", "client_credential");
        param.put("appid", appId);
        param.put("secret", appSec);
        param.put("force_refresh", false);

        AccessToken result = new RestTemplate().postForObject(url, param, AccessToken.class);
        assert result != null;
        log.info("AccessTokenService.obtainAccessToken: {} ...", result);
        return result.getAccess_token();
    }


    @CacheEvict(allEntries = true)
    public void cacheEvict() {
        log.info("cacheEvict cacheNames: {} ...", CACHE_NAME);
    }

}
