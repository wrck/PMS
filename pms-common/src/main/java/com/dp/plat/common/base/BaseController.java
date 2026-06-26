package com.dp.plat.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.R;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(LocalDateTime.parse(text, DATE_TIME_FORMATTER));
            }
        });
    }

    protected <T> R<Page<T>> pageResult(Page<T> page) {
        return R.ok(page);
    }
}
