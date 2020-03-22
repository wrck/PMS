package com.dp.plat.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.User;

public class PermissionTag extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Integer permissionId;

	public Integer getPermissionId()
	{
		return permissionId;
	}

	public void setPermissionId(Integer permissionId)
	{
		this.permissionId = permissionId;
	}

	@Override
	public int doStartTag() throws JspException
	{
		int ret = TagSupport.EVAL_BODY_INCLUDE;
		User user = UserContext.getUserContext().getUser();
		if (user == null)
		{
			return TagSupport.SKIP_PAGE;
		}
//		List<Permissions> permissions = user.getPermissions();
//		boolean find = false;
//		for (Permissions permission : permissions)
//		{
//			if (permissionId == permission.getId())
//			{
//				find = true;
//			}
//		}

//		if (!find)
//		{
//			ret = TagSupport.SKIP_BODY;
//		}
		return ret;
	}
}
