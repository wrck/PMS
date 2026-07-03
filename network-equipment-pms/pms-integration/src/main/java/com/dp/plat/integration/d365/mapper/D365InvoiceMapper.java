package com.dp.plat.integration.d365.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.integration.d365.entity.D365Invoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link D365Invoice}.
 */
@Mapper
public interface D365InvoiceMapper extends BaseMapper<D365Invoice> {
}
