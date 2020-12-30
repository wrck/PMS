package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.dao.IndustryAssetProjectRelationMapper;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import org.springframework.transaction.annotation.Transactional;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.dp.plat.pms.springmvc.vo.ProjectAssetVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetProjectRelationService")
public class IndustryAssetProjectRelationService extends AbstractBaseService<IndustryAssetProjectRelationMapper, IndustryAssetProjectRelation> implements IIndustryAssetProjectRelationService {

    @Lazy
    @Autowired
    private IIndustryAssetService industryAssetService;

    @Override
    public List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam) {
        return dao.selectProjectAssetBySelectivePageable(pageParam);
    }

    @Override
    public long countProjectAssetBySelectivePageable(PageParam<Object> pageParam) {
        return dao.countProjectAssetBySelectivePageable(pageParam);
    }

    @Override
    @Transactional
    public void insertProjectAssetSelective(IndustryAssetVO v) {
        if (v == null) {
            return;
        }
        industryAssetService.insertSelective(v);
        if (((ProjectAssetVO) v).getProjectId() != null) {
            IndustryAssetProjectRelation t = new IndustryAssetProjectRelation();
            t.setAssetId(v.getId());
            t.setProjectId(((ProjectAssetVO) v).getProjectId());
            this.insertSelective(t);
        }
    }

    @Override
    public void invalidAssetProjectRelation(IndustryAssetProjectRelation t) {
        dao.invalidAssetProjectRelation(t);
    }

}
