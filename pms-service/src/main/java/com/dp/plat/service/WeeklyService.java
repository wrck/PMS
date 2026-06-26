package com.dp.plat.service;

import com.dp.plat.model.entity.PmsProjectWeekly;
import com.dp.plat.model.entity.WeeklyContent;
import com.dp.plat.model.entity.WeeklyFeedback;
import java.util.List;

public interface WeeklyService {
    List<PmsProjectWeekly> queryWeeklyList(Long projectId, Integer weeklyState);
    PmsProjectWeekly getWeeklyDetail(Long weeklyId);
    Long createWeekly(PmsProjectWeekly weekly);
    void updateWeekly(PmsProjectWeekly weekly);
    void submitWeekly(Long weeklyId);
    void deleteWeekly(Long weeklyId);
    List<WeeklyContent> queryWeeklyContents(Long weeklyId, Integer optionType);
    void saveWeeklyContents(Long weeklyId, List<WeeklyContent> contents);
    List<WeeklyFeedback> queryFeedbacks(Long weeklyId);
    void addFeedback(WeeklyFeedback feedback);
}
