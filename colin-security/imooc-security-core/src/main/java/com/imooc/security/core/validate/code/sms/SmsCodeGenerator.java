/**
 * 
 */
package com.imooc.security.core.validate.code.sms;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import com.imooc.security.core.properties.CSecurityProperties;
import com.imooc.security.core.validate.code.ValidateCode;
import com.imooc.security.core.validate.code.ValidateCodeGenerator;

/**
 * 短信验证码生成器
 * 
 * @author zhailiang
 *
 */
@Component("smsValidateCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {

	@Autowired
	private CSecurityProperties CSecurityProperties;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imooc.security.core.validate.code.ValidateCodeGenerator#generate(org.
	 * springframework.web.context.request.ServletWebRequest)
	 */
	@Override
	public ValidateCode generate(ServletWebRequest request) {
		String code = RandomStringUtils.randomNumeric(CSecurityProperties.getCode().getSms().getLength());
		return new ValidateCode(code, CSecurityProperties.getCode().getSms().getExpireIn());
	}

	public CSecurityProperties getCSecurityProperties() {
		return CSecurityProperties;
	}

	public void setCSecurityProperties(CSecurityProperties CSecurityProperties) {
		this.CSecurityProperties = CSecurityProperties;
	}
	
	

}
