/**
 *
 */
package com.imooc.security.core.authorize;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认的授权配置管理器
 *
 * @author 蔡勇刚
 *
 */
@Component
public class DefaultAuthorizeHttpSecurityConfigManager implements AuthorizeHttpSecurityConfigManager {

    @Autowired(required = false)
    private List<AuthorizeHttpSecurityConfigProvider> httpSecurityConfigProviders;

    /* (non-Javadoc)
     * @see com.imooc.security.core.authorize.AuthorizeConfigManager#config(org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry)
     */
    @Override
    public void config(HttpSecurity http) throws Exception {
        if (CollectionUtils.isEmpty(httpSecurityConfigProviders)) {
            return;
        }
        boolean existAnyRequestConfig = false;
        String existAnyRequestConfigName = null;

        for (AuthorizeHttpSecurityConfigProvider httpSecurityConfigProvider : httpSecurityConfigProviders) {
            boolean currentIsAnyRequestConfig = httpSecurityConfigProvider.config(http);
            if (existAnyRequestConfig && currentIsAnyRequestConfig) {
                throw new RuntimeException("重复的anyRequest配置:" + existAnyRequestConfigName + ","
                        + httpSecurityConfigProvider.getClass().getSimpleName());
            } else if (currentIsAnyRequestConfig) {
                existAnyRequestConfig = true;
                existAnyRequestConfigName = httpSecurityConfigProvider.getClass().getSimpleName();
            }
        }
    }

}
