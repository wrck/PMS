package com.dp.plat.common.test;

import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test data builder utility providing common helpers for constructing
 * test objects across modules.
 *
 * <p>Domain-specific entities (Project, Asset, ImplTask, etc.) carry a
 * Lombok {@code @Builder} annotation; tests should use those builders
 * directly. This class focuses on shared concerns: id generation,
 * timestamp helpers and common {@link BaseEntity} / {@link Result}
 * scaffolding.</p>
 */
public final class TestDataBuilder {

    /** Monotonic id generator so tests get distinct ids without a database. */
    private static final AtomicLong ID_SEQ = new AtomicLong(1000L);

    private TestDataBuilder() {
    }

    /**
     * Generate a unique positive id for test entities.
     *
     * @return next id in the sequence
     */
    public static Long nextId() {
        return ID_SEQ.incrementAndGet();
    }

    /**
     * Reset the id sequence back to the initial value.
     */
    public static void resetIdSequence() {
        ID_SEQ.set(1000L);
    }

    /**
     * @return a fixed create-time timestamp for deterministic tests.
     */
    public static LocalDateTime fixedCreateTime() {
        return LocalDateTime.of(2024, 1, 15, 10, 0, 0);
    }

    /**
     * @return a fixed update-time timestamp for deterministic tests.
     */
    public static LocalDateTime fixedUpdateTime() {
        return LocalDateTime.of(2024, 1, 16, 12, 30, 0);
    }

    /**
     * Populate the audit fields of a {@link BaseEntity} with default test values.
     *
     * @param entity      target entity
     * @param id          id to assign (nullable)
     * @param createBy    creator name
     * @param <T>         entity type
     * @return the same entity instance for chaining
     */
    public static <T extends BaseEntity> T withAudit(T entity, Long id, String createBy) {
        if (entity == null) {
            return null;
        }
        entity.setId(id);
        entity.setCreateTime(fixedCreateTime());
        entity.setUpdateTime(fixedUpdateTime());
        entity.setCreateBy(createBy);
        entity.setUpdateBy(createBy);
        entity.setDeleted(0);
        return entity;
    }

    /**
     * Build a success {@link Result} carrying the given payload.
     *
     * @param data payload
     * @param <T>  payload type
     * @return success result
     */
    public static <T> Result<T> okResult(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * Build an error {@link Result} with the given message.
     *
     * @param message error message
     * @param <T>     payload type
     * @return error result
     */
    public static <T> Result<T> failResult(String message) {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * Instantiate a BaseEntity subtype using its no-arg constructor via reflection.
     * Useful for tests that need a fresh entity without using the Lombok builder.
     *
     * @param clazz entity class
     * @param <T>   entity type
     * @return new instance
     */
    public static <T extends BaseEntity> T newEntity(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate " + clazz.getName(), e);
        }
    }
}
