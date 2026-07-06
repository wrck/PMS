package com.dp.plat.project.deliverable.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;
import com.dp.plat.project.deliverable.enums.DeliverableType;
import com.dp.plat.project.deliverable.mapper.DeliverableChecklistMapper;
import com.dp.plat.project.deliverable.service.impl.DeliverableChecklistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DeliverableChecklistServiceImpl}.
 *
 * <p>由于 MyBatis-Plus 的 {@code saveBatch} 依赖 SqlSessionFactory（不能仅靠 mock 的 Mapper 运行），
 * 测试中通过 {@link Mockito#spy} 包装真实 service 实例并 stub {@code saveBatch} 返回 true，
 * 其余方法（save/getById/updateById/removeById/list 等）走 baseMapper 直接调用，可正常 mock。</p>
 */
@ExtendWith(MockitoExtension.class)
class DeliverableChecklistServiceImplTest {

    @Mock
    private DeliverableChecklistMapper deliverableChecklistMapper;

    private DeliverableChecklistServiceImpl deliverableChecklistService;

    @BeforeEach
    void setUp() {
        // 使用 spy 包装真实实例，便于 stub 依赖 SqlSession 的 saveBatch 方法
        deliverableChecklistService = Mockito.spy(new DeliverableChecklistServiceImpl());
        // ServiceImpl.baseMapper 必须手动注入，@InjectMocks 不会注入基类字段
        ReflectionTestUtils.setField(deliverableChecklistService, "baseMapper", deliverableChecklistMapper);
    }

    private DeliverableChecklist sampleChecklist(Long id, Long projectId, String type, boolean required, boolean uploaded) {
        DeliverableChecklist item = DeliverableChecklist.builder()
                .projectId(projectId)
                .deliverableType(type)
                .required(required)
                .uploaded(uploaded)
                .build();
        item.setId(id);
        return item;
    }

    @Test
    @DisplayName("create: 入参为 null 抛出业务异常")
    void create_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> deliverableChecklistService.create(null));
        assertTrue(ex.getMessage().contains("交付物清单"));
        verify(deliverableChecklistMapper, never()).insert(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("create: 缺少 projectId 抛出业务异常")
    void create_missingProjectId_throws() {
        DeliverableChecklist input = DeliverableChecklist.builder()
                .deliverableType(DeliverableType.AS_BUILT.name())
                .build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> deliverableChecklistService.create(input));
        assertTrue(ex.getMessage().contains("项目ID"));
        verify(deliverableChecklistMapper, never()).insert(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("create: 缺省 required=true / uploaded=false 并清空 id 后保存")
    void create_shouldApplyDefaultsAndSave() {
        DeliverableChecklist input = DeliverableChecklist.builder()
                .projectId(10L)
                .deliverableType(DeliverableType.AS_BUILT.name())
                .build();
        input.setId(999L);  // 预设一个 id，验证 create 会清空它
        // 用 thenAnswer 验证插入时 id 已被清空为 null
        when(deliverableChecklistMapper.insert(any(DeliverableChecklist.class))).thenAnswer(invocation -> {
            DeliverableChecklist entity = invocation.getArgument(0);
            assertNull(entity.getId(), "插入前 id 应被清空为 null");
            entity.setId(1L);
            return 1;
        });

        Result<DeliverableChecklist> result = deliverableChecklistService.create(input);

        assertTrue(result.isSuccess());
        assertEquals(true, result.getData().getRequired(), "required 缺省应为 true");
        assertEquals(false, result.getData().getUploaded(), "uploaded 缺省应为 false");
        verify(deliverableChecklistMapper, times(1)).insert(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("create: 显式 required=false 时保留 false")
    void create_shouldKeepExplicitRequired() {
        DeliverableChecklist input = DeliverableChecklist.builder()
                .projectId(10L)
                .deliverableType(DeliverableType.AS_BUILT.name())
                .required(false)
                .uploaded(true)
                .build();
        when(deliverableChecklistMapper.insert(any(DeliverableChecklist.class))).thenReturn(1);

        Result<DeliverableChecklist> result = deliverableChecklistService.create(input);

        assertTrue(result.isSuccess());
        assertEquals(false, result.getData().getRequired());
        assertEquals(true, result.getData().getUploaded());
    }

    @Test
    @DisplayName("update: 入参为 null 抛出业务异常")
    void update_null_throws() {
        assertThrows(BusinessException.class, () -> deliverableChecklistService.update((DeliverableChecklist) null));
        verify(deliverableChecklistMapper, never()).updateById(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("update: 缺少 id 抛出业务异常")
    void update_missingId_throws() {
        DeliverableChecklist input = DeliverableChecklist.builder().build();
        assertThrows(BusinessException.class, () -> deliverableChecklistService.update(input));
        verify(deliverableChecklistMapper, never()).updateById(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("update: 记录不存在抛出业务异常")
    void update_notFound_throws() {
        when(deliverableChecklistMapper.selectById(anyLong())).thenReturn(null);
        DeliverableChecklist input = DeliverableChecklist.builder().build();
        input.setId(99L);
        assertThrows(BusinessException.class, () -> deliverableChecklistService.update(input));
        verify(deliverableChecklistMapper, never()).updateById(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("update: 已存在记录更新成功")
    void update_existing_succeeds() {
        DeliverableChecklist existing = sampleChecklist(1L, 10L, DeliverableType.AS_BUILT.name(), true, false);
        when(deliverableChecklistMapper.selectById(1L)).thenReturn(existing);
        when(deliverableChecklistMapper.updateById(any(DeliverableChecklist.class))).thenReturn(1);

        DeliverableChecklist toUpdate = DeliverableChecklist.builder().build();
        toUpdate.setId(1L);
        toUpdate.setUploaded(true);
        Result<?> result = deliverableChecklistService.update(toUpdate);

        assertTrue(result.isSuccess());
        verify(deliverableChecklistMapper, times(1)).updateById(any(DeliverableChecklist.class));
    }

    @Test
    @DisplayName("delete: 记录不存在抛出业务异常")
    void delete_notFound_throws() {
        when(deliverableChecklistMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> deliverableChecklistService.delete(99L));
        verify(deliverableChecklistMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("delete: 已存在记录删除成功")
    void delete_existing_succeeds() {
        DeliverableChecklist existing = sampleChecklist(1L, 10L, DeliverableType.AS_BUILT.name(), true, false);
        when(deliverableChecklistMapper.selectById(1L)).thenReturn(existing);
        // ServiceImpl.removeById 依赖 TableInfo（单元测试中为 null），stub 为 true
        doReturn(true).when(deliverableChecklistService).removeById(1L);

        Result<?> result = deliverableChecklistService.delete(1L);

        assertTrue(result.isSuccess());
        verify(deliverableChecklistService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("getById: id 为 null 抛出业务异常")
    void getById_nullId_throws() {
        assertThrows(BusinessException.class, () -> deliverableChecklistService.getById(null));
    }

    @Test
    @DisplayName("getById: 记录不存在抛出业务异常")
    void getById_notFound_throws() {
        when(deliverableChecklistMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> deliverableChecklistService.getById(99L));
    }

    @Test
    @DisplayName("getById: 返回交付物清单项")
    void getById_found() {
        DeliverableChecklist item = sampleChecklist(1L, 10L, DeliverableType.AS_BUILT.name(), true, true);
        when(deliverableChecklistMapper.selectById(1L)).thenReturn(item);

        Result<DeliverableChecklist> result = deliverableChecklistService.getById(1L);

        assertTrue(result.isSuccess());
        assertEquals(DeliverableType.AS_BUILT.name(), result.getData().getDeliverableType());
    }

    @Test
    @DisplayName("listByProject: projectId 为 null 抛出业务异常")
    void listByProject_nullId_throws() {
        assertThrows(BusinessException.class, () -> deliverableChecklistService.listByProject(null));
    }

    @Test
    @DisplayName("listByProject: 返回项目下交付物清单")
    void listByProject_returnsList() {
        List<DeliverableChecklist> list = Arrays.asList(
                sampleChecklist(1L, 10L, DeliverableType.AS_BUILT.name(), true, true),
                sampleChecklist(2L, 10L, DeliverableType.TEST_REPORT.name(), true, false));
        when(deliverableChecklistMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<DeliverableChecklist>> result = deliverableChecklistService.listByProject(10L);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("initChecklist: projectId 为 null 抛出业务异常")
    void initChecklist_nullId_throws() {
        assertThrows(BusinessException.class, () -> deliverableChecklistService.initChecklist(null));
    }

    @Test
    @DisplayName("initChecklist: 已存在记录时直接返回，不重复初始化")
    void initChecklist_existing_returnsExisting() {
        List<DeliverableChecklist> existing = Collections.singletonList(
                sampleChecklist(1L, 10L, DeliverableType.AS_BUILT.name(), true, true));
        when(deliverableChecklistMapper.selectList(any(Wrapper.class))).thenReturn(existing);

        Result<List<DeliverableChecklist>> result = deliverableChecklistService.initChecklist(10L);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size(), "应返回已存在的记录");
        verify(deliverableChecklistService, never()).saveBatch(anyList());
    }

    @Test
    @DisplayName("initChecklist: 无记录时为每种 DeliverableType 创建一条记录")
    void initChecklist_empty_createsAllTypes() {
        when(deliverableChecklistMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());
        // saveBatch 依赖 SqlSession，stub 为 true 以跳过实际批量插入
        doReturn(true).when(deliverableChecklistService).saveBatch(anyList());

        Result<List<DeliverableChecklist>> result = deliverableChecklistService.initChecklist(10L);

        assertTrue(result.isSuccess());
        List<DeliverableChecklist> records = result.getData();
        assertNotNull(records);
        assertEquals(DeliverableType.values().length, records.size(), "应为每种交付物类型创建一条记录");
        // 校验第一条记录字段
        DeliverableChecklist first = records.get(0);
        assertEquals(10L, first.getProjectId());
        assertEquals(true, first.getRequired());
        assertEquals(false, first.getUploaded());
        verify(deliverableChecklistService, times(1)).saveBatch(anyList());
    }

    @Test
    @DisplayName("initChecklist: 初始化记录覆盖所有 DeliverableType 枚举值")
    void initChecklist_shouldCoverAllDeliverableTypes() {
        when(deliverableChecklistMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());
        doReturn(true).when(deliverableChecklistService).saveBatch(anyList());

        Result<List<DeliverableChecklist>> result = deliverableChecklistService.initChecklist(10L);

        List<String> createdTypes = result.getData().stream()
                .map(DeliverableChecklist::getDeliverableType)
                .toList();
        for (DeliverableType type : DeliverableType.values()) {
            assertTrue(createdTypes.contains(type.name()), "应包含交付物类型: " + type.name());
        }
        // 验证没有重复
        assertEquals(DeliverableType.values().length, createdTypes.size());
    }
}
