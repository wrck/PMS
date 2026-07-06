package com.dp.plat.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.notification.entity.NotificationTemplate;
import com.dp.plat.notification.mapper.NotificationTemplateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link NotificationTemplateServiceImpl} 的单元测试。
 *
 * <p>覆盖模板 CRUD（含继承自 ServiceImpl 的 getById/removeById/list）、按编码查询、
 * 分页查询，以及自定义 save/updateById 中的时间戳填充逻辑。</p>
 */
@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceImplTest {

    @Mock
    private NotificationTemplateMapper templateMapper;

    private NotificationTemplateServiceImpl templateService;

    @BeforeEach
    void setUp() {
        templateService = new NotificationTemplateServiceImpl();
        // ServiceImpl 的 baseMapper 字段无法通过构造注入，需通过反射注入 mock
        ReflectionTestUtils.setField(templateService, "baseMapper", templateMapper);
    }

    private NotificationTemplate sampleTemplate(Long id, String code) {
        NotificationTemplate t = NotificationTemplate.builder()
                .templateCode(code)
                .subject("Subject " + code)
                .body("Body ${var}")
                .variables("[\"var\"]")
                .description("desc")
                .build();
        t.setId(id);
        return t;
    }

    // ==================== getByCode ====================

    @Test
    @DisplayName("getByCode: 模板存在时返回实体")
    void getByCode_found() {
        NotificationTemplate t = sampleTemplate(1L, "TASK_ASSIGNED");
        when(templateMapper.selectOne(any(Wrapper.class))).thenReturn(t);

        NotificationTemplate result = templateService.getByCode("TASK_ASSIGNED");

        assertNotNull(result);
        assertEquals("TASK_ASSIGNED", result.getTemplateCode());
    }

    @Test
    @DisplayName("getByCode: 模板不存在时返回 null")
    void getByCode_notFound() {
        when(templateMapper.selectOne(any(Wrapper.class))).thenReturn(null);

        NotificationTemplate result = templateService.getByCode("NOT_EXIST");

        assertNull(result);
    }

    // ==================== list(int, int) ====================

    @Test
    @DisplayName("list: 返回分页结果")
    void list_paged_returnsPage() {
        Page<NotificationTemplate> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
                sampleTemplate(1L, "T1"),
                sampleTemplate(2L, "T2")));
        mockPage.setTotal(2L);
        when(templateMapper.selectPage(any(Page.class), any(Wrapper.class))).thenReturn(mockPage);

        IPage<NotificationTemplate> result = templateService.list(1, 10);

        assertEquals(2, result.getRecords().size());
        assertEquals(2L, result.getTotal());
    }

    // ==================== save ====================

    @Test
    @DisplayName("save: 新建模板时 createdAt 为 null 自动填充，updatedAt 一并填充")
    void save_new_setsCreatedAtAndUpdatedAt() {
        NotificationTemplate t = sampleTemplate(null, "NEW_TPL");
        when(templateMapper.insert(any(NotificationTemplate.class))).thenReturn(1);

        boolean result = templateService.save(t);

        assertTrue(result);
        assertNotNull(t.getCreatedAt(), "createdAt 应被填充");
        assertNotNull(t.getUpdatedAt(), "updatedAt 应被填充");
        verify(templateMapper, times(1)).insert(any(NotificationTemplate.class));
    }

    @Test
    @DisplayName("save: 已有 createdAt 时保留原值，仅更新 updatedAt")
    void save_existingCreatedAt_keepsCreatedAt() {
        LocalDateTime original = LocalDateTime.of(2024, 1, 1, 10, 0);
        NotificationTemplate t = sampleTemplate(null, "NEW_TPL");
        t.setCreatedAt(original);
        when(templateMapper.insert(any(NotificationTemplate.class))).thenReturn(1);

        templateService.save(t);

        assertEquals(original, t.getCreatedAt(), "已有 createdAt 不应被覆盖");
        assertNotNull(t.getUpdatedAt());
    }

    @Test
    @DisplayName("save: mapper.insert 返回 0 时返回 false")
    void save_insertFailure_returnsFalse() {
        NotificationTemplate t = sampleTemplate(null, "NEW_TPL");
        when(templateMapper.insert(any(NotificationTemplate.class))).thenReturn(0);

        boolean result = templateService.save(t);

        assertFalse(result);
    }

    // ==================== updateById ====================

    @Test
    @DisplayName("updateById: 更新时填充 updatedAt 并返回 true")
    void updateById_setsUpdatedAt() {
        NotificationTemplate t = sampleTemplate(1L, "T1");
        when(templateMapper.updateById(any(NotificationTemplate.class))).thenReturn(1);

        boolean result = templateService.updateById(t);

        assertTrue(result);
        assertNotNull(t.getUpdatedAt());
        verify(templateMapper, times(1)).updateById(any(NotificationTemplate.class));
    }

    @Test
    @DisplayName("updateById: mapper.updateById 返回 0 时返回 false")
    void updateById_failure_returnsFalse() {
        NotificationTemplate t = sampleTemplate(1L, "T1");
        when(templateMapper.updateById(any(NotificationTemplate.class))).thenReturn(0);

        boolean result = templateService.updateById(t);

        assertFalse(result);
    }

    // ==================== getById (继承自 ServiceImpl) ====================

    @Test
    @DisplayName("getById: 返回模板详情")
    void getById_found() {
        NotificationTemplate t = sampleTemplate(1L, "T1");
        when(templateMapper.selectById(1L)).thenReturn(t);

        NotificationTemplate result = templateService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById: 不存在时返回 null")
    void getById_notFound() {
        when(templateMapper.selectById(anyLong())).thenReturn(null);

        NotificationTemplate result = templateService.getById(99L);

        assertNull(result);
    }

    // ==================== removeById (继承自 ServiceImpl) ====================
    // 注：ServiceImpl.removeById(Serializable) 单参版本直接调用 TableInfo.isWithLogicDelete()
    // 在纯单元测试中 TableInfo 未初始化（为 null）会导致 NPE。
    // 双参版本 removeById(id, useFill) 在 useFill=false 时会跳过 TableInfo 检查直接调用 deleteById，
    // 因此这里使用双参版本以验证删除委托逻辑。

    @Test
    @DisplayName("removeById: 删除成功返回 true")
    void removeById_success() {
        when(templateMapper.deleteById(1L)).thenReturn(1);

        boolean result = templateService.removeById(1L, false);

        assertTrue(result);
        verify(templateMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("removeById: deleteById 返回 0 时返回 false")
    void removeById_failure_returnsFalse() {
        when(templateMapper.deleteById(99L)).thenReturn(0);

        boolean result = templateService.removeById(99L, false);

        assertFalse(result);
    }

    // ==================== list() 继承自 ServiceImpl ====================

    @Test
    @DisplayName("list: 无参数版返回全部模板列表")
    void listAll_returnsList() {
        List<NotificationTemplate> list = Arrays.asList(
                sampleTemplate(1L, "T1"),
                sampleTemplate(2L, "T2"));
        when(templateMapper.selectList(any())).thenReturn(list);

        List<NotificationTemplate> result = templateService.list();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("list: 库中无模板时返回空列表")
    void listAll_empty_returnsEmptyList() {
        when(templateMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<NotificationTemplate> result = templateService.list();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(templateMapper, never()).insert(any(NotificationTemplate.class));
    }
}
