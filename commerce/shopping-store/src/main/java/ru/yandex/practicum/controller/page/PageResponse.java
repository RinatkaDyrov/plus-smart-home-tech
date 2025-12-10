package ru.yandex.practicum.controller.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        List<SortOrder> sort
) {
    public PageResponse(Page<T> page) {
        this(
                page.getContent(),
                page.getSort()
                        .stream()
                        .map(SortOrder::new)
                        .toList()
        );
    }

    public record SortOrder(String property, String direction) {
        public SortOrder(Sort.Order order) {
            this(order.getProperty(), order.getDirection().name());
        }
    }
}
