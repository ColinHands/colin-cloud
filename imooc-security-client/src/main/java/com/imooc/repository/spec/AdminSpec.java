/**
 * 
 */
package com.imooc.repository.spec;

import com.imooc.dto.AdminCondition;
import com.imooc.entity.Admin;
import com.imooc.repository.support.ImoocSpecification;
import com.imooc.repository.support.QueryWraper;

/**
 * @author colin cai
 *
 */
public class AdminSpec extends ImoocSpecification<Admin, AdminCondition> {

	public AdminSpec(AdminCondition condition) {
		super(condition);
	}

	@Override
	protected void addCondition(QueryWraper<Admin> queryWraper) {
		addLikeCondition(queryWraper, "username");
	}

}
