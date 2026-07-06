package com.dp.plat.admin.service;

import com.dp.plat.admin.dto.ActivityItem;
import com.dp.plat.admin.dto.DashboardStats;
import com.dp.plat.admin.dto.ProjectTrendItem;
import com.dp.plat.admin.dto.TodoItem;

import java.util.List;

/**
 * Aggregation service that powers the dashboard home page.
 *
 * <p>Cross-module statistics are computed here by querying the project, asset,
 * implementation and system-log mappers. Heavy aggregate queries are cached
 * via Spring Cache ({@code @Cacheable}) to keep the dashboard responsive.</p>
 */
public interface ReportService {

    /**
     * Aggregate the headline numbers shown on the dashboard overview cards.
     *
     * @return dashboard statistics
     */
    DashboardStats getDashboardStats();

    /**
     * Project trend over the last 6 months grouped by month and status.
     *
     * @return list of trend items, ordered by month asc then status
     */
    List<ProjectTrendItem> getProjectTrend();

    /**
     * Top-N todo items for the current user, ordered by deadline asc.
     *
     * @param limit max number of items
     * @return todo items
     */
    List<TodoItem> getTodoList(int limit);

    /**
     * Most recent activity logs (operation logs + login logs), top-N.
     *
     * @param limit max number of items
     * @return activity items, newest first
     */
    List<ActivityItem> getRecentActivities(int limit);
}
