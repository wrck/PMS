package com.dp.plat.project.punchlist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.punchlist.entity.PunchList;

import java.util.List;

/**
 * Service for {@link PunchList}.
 */
public interface IPunchListService extends IService<PunchList> {

    /**
     * Create a punch list item. Safety-severity items block the related milestone.
     *
     * @param punchList punch list item to create
     * @return operation result containing the created item
     */
    Result<PunchList> create(PunchList punchList);

    /**
     * Update a punch list item.
     *
     * @param punchList punch list item to update
     * @return operation result
     */
    Result<?> update(PunchList punchList);

    /**
     * Delete a punch list item by id.
     *
     * @param id punch list item id
     * @return operation result
     */
    Result<?> delete(Long id);

    /**
     * Get a punch list item by id.
     *
     * @param id punch list item id
     * @return operation result containing the item
     */
    Result<PunchList> getById(Long id);

    /**
     * List punch list items by project id.
     *
     * @param projectId project id
     * @return operation result containing the item list
     */
    Result<List<PunchList>> listByProject(Long projectId);

    /**
     * List punch list items by milestone id.
     *
     * @param milestoneId milestone id
     * @return operation result containing the item list
     */
    Result<List<PunchList>> listByMilestone(Long milestoneId);

    /**
     * Mark a punch list item as resolved.
     *
     * @param id punch list item id
     * @return operation result containing the updated item
     */
    Result<PunchList> resolve(Long id);

    /**
     * Mark a resolved punch list item as verified.
     *
     * @param id punch list item id
     * @return operation result containing the updated item
     */
    Result<PunchList> verify(Long id);

    /**
     * Check whether all punch list items for a project are verified.
     *
     * @param projectId project id
     * @return {@code true} if all items are verified (or there are no items)
     */
    boolean isAllVerified(Long projectId);
}
