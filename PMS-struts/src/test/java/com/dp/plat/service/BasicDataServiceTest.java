package com.dp.plat.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.data.bean.BasicDataBean;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class BasicDataServiceTest {

    @Mock
    private BasicDataDao basicDataDao;
    
    private BasicDataServiceImpl basicDataService;
    
    @Before
    public void setUp() {
        basicDataService = new BasicDataServiceImpl();
        basicDataService.setBasicDataDao(basicDataDao);
    }

    @Test
    public void testQueryBasicDataBeans() {
        String dataType = "PROJECT_TYPE";
        List<BasicDataBean> mockList = new ArrayList<>();
        BasicDataBean bean = new BasicDataBean();
        bean.setBasicDataId("1");
        bean.setBasicDataName("项目类型1");
        mockList.add(bean);
        
        Mockito.when(basicDataDao.queryBasicDataBeans(dataType)).thenReturn(mockList);
        
        List<BasicDataBean> result = basicDataService.queryBasicDataBeans(dataType);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getBasicDataId());
        assertEquals("项目类型1", result.get(0).getBasicDataName());
    }

    @Test
    public void testQueryBasicDataBeans_Empty() {
        String dataType = "NON_EXISTENT";
        Mockito.when(basicDataDao.queryBasicDataBeans(dataType)).thenReturn(new ArrayList<>());
        
        List<BasicDataBean> result = basicDataService.queryBasicDataBeans(dataType);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testQueryBasicDataType() {
        List<BasicDataBean> mockList = new ArrayList<>();
        BasicDataBean bean1 = new BasicDataBean();
        bean1.setBasicDataTypeCode("PROJECT_TYPE");
        BasicDataBean bean2 = new BasicDataBean();
        bean2.setBasicDataTypeCode("USER_TYPE");
        mockList.add(bean1);
        mockList.add(bean2);
        
        Mockito.when(basicDataDao.queryBasicDataType()).thenReturn(mockList);
        
        List<BasicDataBean> result = basicDataService.queryBasicDataType();
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testQueryBasicDataBean() {
        String id = "1";
        BasicDataBean mockBean = new BasicDataBean();
        mockBean.setBasicDataId(id);
        mockBean.setBasicDataName("测试数据");
        
        Mockito.when(basicDataDao.queryBasicDataBean(Integer.parseInt(id))).thenReturn(mockBean);
        
        BasicDataBean result = basicDataService.queryBasicDataBean(Integer.parseInt(id));
        
        assertNotNull(result);
        assertEquals(id, result.getBasicDataId());
        assertEquals("测试数据", result.getBasicDataName());
    }

    @Test
    public void testQueryBasicDataBean_NotFound() {
        int id = 999;
        Mockito.when(basicDataDao.queryBasicDataBean(id)).thenReturn(null);
        
        BasicDataBean result = basicDataService.queryBasicDataBean(id);
        
        assertNull(result);
    }

    @Test
    public void testQueryBasicDataBeanAll() {
        String dataType = "PROJECT_TYPE";
        List<BasicDataBean> mockList = new ArrayList<>();
        BasicDataBean bean = new BasicDataBean();
        bean.setBasicDataId("1");
        bean.setBasicDataTypeCode(dataType);
        mockList.add(bean);
        
        Mockito.when(basicDataDao.queryBasicDataBeanAll(dataType)).thenReturn(mockList);
        
        List<BasicDataBean> result = basicDataService.queryBasicDataBeanAll(dataType);
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindBasicDataId() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("basicDataType", "PROJECT_TYPE");
        paramMap.put("basicDataName", "项目类型1");
        
        Mockito.when(basicDataDao.findBasicDataId(paramMap)).thenReturn(1);
        
        int result = basicDataService.findBasicDataId(paramMap);
        
        assertEquals(1, result);
    }

    @Test
    public void testQuerySysArg() {
        String code = "SYS_VERSION";
        Mockito.when(basicDataDao.querySysArg(code)).thenReturn("1.0.0");
        
        String result = basicDataService.querySysArg(code);
        
        assertEquals("1.0.0", result);
    }

    @Test
    public void testQuerySysArg_NotFound() {
        String code = "NON_EXISTENT";
        Mockito.when(basicDataDao.querySysArg(code)).thenReturn(null);
        
        String result = basicDataService.querySysArg(code);
        
        assertNull(result);
    }

    @Test
    public void testExecuteSql() {
        String sql = "UPDATE basic_data SET status = 1 WHERE id = 1";
        
        // 验证SQL执行不抛异常
        basicDataService.executeSql(sql);
        
        Mockito.verify(basicDataDao).executeSql(sql);
    }

    @Test
    public void testUpdateBasicData() {
        BasicDataBean bean = new BasicDataBean();
        bean.setBasicDataId("1");
        bean.setBasicDataName("更新后的名称");
        
        basicDataService.updateBasicData(bean);
        
        Mockito.verify(basicDataDao).updateBasicData(bean);
    }

    @Test
    public void testDeleteFile() {
        int fileId = 1;
        
        basicDataService.deleteFile(fileId);
        
        Mockito.verify(basicDataDao).deleteFile(fileId);
    }

    @Test
    public void testQueryBasicDataBeanByAttri() {
        String dataType = "PROJECT_TYPE";
        String attri1 = "attr1";
        List<BasicDataBean> mockList = new ArrayList<>();
        BasicDataBean bean = new BasicDataBean();
        bean.setBasicDataId("1");
        bean.setBasicDataAttri1(attri1);
        mockList.add(bean);
        
        Mockito.when(basicDataDao.queryBasicDataBeanByAttri(dataType, attri1)).thenReturn(mockList);
        
        List<BasicDataBean> result = basicDataService.queryBasicDataBeanByAttri(dataType, attri1);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(attri1, result.get(0).getBasicDataAttri1());
    }
}
