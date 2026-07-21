package com.dp.plat.deliverable.service;

import com.dp.plat.common.dto.StoredBusinessFile;
import com.dp.plat.common.spi.BusinessFileStorage;
import com.dp.plat.common.spi.ProjectPhaseLookup;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.entity.DeliverableVersion;
import com.dp.plat.deliverable.mapper.DeliverableMapper;
import com.dp.plat.deliverable.mapper.DeliverableReferenceMapper;
import com.dp.plat.deliverable.mapper.DeliverableSignatureMapper;
import com.dp.plat.deliverable.mapper.DeliverableVersionMapper;
import com.dp.plat.deliverable.service.impl.DeliverableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliverableInitialUploadTest {
    private final DeliverableMapper deliverableMapper = mock(DeliverableMapper.class);
    private final DeliverableVersionMapper versionMapper = mock(DeliverableVersionMapper.class);
    private final BusinessFileStorage fileStorage = mock(BusinessFileStorage.class);
    private DeliverableServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeliverableServiceImpl(
                versionMapper,
                mock(DeliverableSignatureMapper.class),
                mock(DeliverableReferenceMapper.class),
                fileStorage,
                mock(ProjectPhaseLookup.class));
        ReflectionTestUtils.setField(service, "baseMapper", deliverableMapper);
    }

    @Test
    void uploadInitialVersionCreatesVersionAndUpdatesRoot() {
        Deliverable deliverable = Deliverable.builder()
                .projectId(10L)
                .phaseId(20L)
                .deliverableName("设计文档")
                .status("DRAFT")
                .currentVersion(1)
                .build();
        deliverable.setId(30L);
        when(deliverableMapper.selectById(30L)).thenReturn(deliverable);
        when(versionMapper.selectCount(any())).thenReturn(0L);
        when(fileStorage.upload(any(), eq("DELIVERABLE"), eq(30L))).thenReturn(
                StoredBusinessFile.builder()
                        .attachmentId(40L)
                        .accessPath("/api/file/40/download")
                        .uploadedBy(50L)
                        .build());

        DeliverableVersion version = service.uploadInitialVersion(
                30L,
                new MockMultipartFile("file", "design.docx", "application/octet-stream", new byte[]{1}),
                "初始提交");

        assertEquals(1, version.getVersionNo());
        assertEquals("/api/file/40/download", version.getFilePath());
        assertEquals("/api/file/40/download", deliverable.getFilePath());
        verify(versionMapper).insert(any(DeliverableVersion.class));
        verify(deliverableMapper).updateById(deliverable);
    }
}
