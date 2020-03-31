package com.dp.plat.pms.springmvc.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.DispatchType;
import com.dp.plat.pms.springmvc.dao.DispatchProjectMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchProjectService")
public class DispatchProjectService extends AbstractBaseService<DispatchProjectMapper, DispatchProject>
		implements IDispatchProjectService {

	@Override
	public void insertOrUpdateSelective(DispatchProject dispatch) {
		dao.insertOrUpdateSelective(dispatch);
	}

	@Override
	@Transactional
	public void dispatchSubmit(Integer id, DispatchVO dispatch) {
		String dispatchSeq = dispatch.getDispatchSeq();
		String facilitatorCode = dispatch.getFacilitatorCode();
		String dispatchNo = dispatch.getDispatchNo();
		Date dispatchTime = new Date();
		if (StringUtils.isBlank(dispatchSeq)) {
			// 查询服务商的项目派单顺序
			DispatchProject temp = new DispatchProject();
			temp.setDispatched(true);
			temp.setFacilitatorCode(facilitatorCode);
			long count = this.countBySelective(temp);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			Object[] seqs = new Object[] { year, facilitatorCode, count + 1 };
			dispatchSeq = StringUtils.join(seqs, "-");
		}
		if (DispatchType.FRAMEWORK_AGREEMENT.equals(dispatch.getType()) && StringUtils.isBlank(dispatchNo)) {
			String dispatchTimeStr = DateFormatUtils.format(dispatchTime, "yyyyMMdd");
			dispatchNo = ProjectConstant.DispatchNOPrefix.AF + dispatchTimeStr + dispatchSeq.replaceAll("-", "");
		}
		DispatchProject temp = new DispatchProject();
		temp.setId(id);
		temp.setDispatchTime(dispatchTime);
		temp.setDispatchSeq(dispatchSeq);
		temp.setDispatchNo(dispatchNo);
		// 派单执行中
		temp.setState(50);
		temp.setDispatched(true);
		this.updateByPrimaryKeySelective(temp);
	}
}
