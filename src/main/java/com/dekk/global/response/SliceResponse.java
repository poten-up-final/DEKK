package com.dekk.global.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(List<T> content, int currentPage, int size, boolean hasNext) {
    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }
}
