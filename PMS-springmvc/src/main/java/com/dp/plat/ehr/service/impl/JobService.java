package com.dp.plat.ehr.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.ehr.dao.JobMapper;
import com.dp.plat.ehr.entity.Job;
import com.dp.plat.ehr.service.IJobService;

/**
 *
 * Created by CodeGenerator
 */
@Service("jobService")
public class JobService extends AbstractBaseService<JobMapper, Job> implements IJobService {
}
