package com.adlph.internal.managment.index.server.common;

import com.adlph.internal.managment.index.server.api.rest.data.ApiResponse;
import com.adlph.internal.managment.index.server.api.rest.data.PageCountResponse;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void okWithData() {
        ApiResponse<String> r = ApiResponse.ok("hello");
        assertEquals(0, r.getCode());
        assertEquals("Ok", r.getMessage());
        assertEquals("hello", r.getData());
    }

    @Test
    void okWithNull() {
        ApiResponse<Void> r = ApiResponse.ok(null);
        assertEquals(0, r.getCode());
        assertEquals("Ok", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void okWithList() {
        var r = ApiResponse.ok(List.of("a", "b"));
        assertEquals(2, r.getData().size());
    }

    @Test
    void error() {
        ApiResponse<Void> r = ApiResponse.error(-1, "Server error");
        assertEquals(-1, r.getCode());
        assertEquals("Server error", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void errorWithDifferentCode() {
        ApiResponse<Void> r = ApiResponse.error(1, "Not found");
        assertEquals(1, r.getCode());
    }

    @Test
    void builder() {
        var r = ApiResponse.<Map<String, Integer>>builder()
            .code(0).message("Ok").data(Map.of("key", 42)).build();
        assertEquals(0, r.getCode());
        assertEquals(42, r.getData().get("key"));
    }

    @Test
    void setters() {
        var r = ApiResponse.<String>builder().build();
        r.setCode(2);
        r.setMessage("msg");
        r.setData("x");
        assertEquals(2, r.getCode());
        assertEquals("msg", r.getMessage());
        assertEquals("x", r.getData());
    }

    @Test
    void pageCountResponse() {
        var p = PageCountResponse.builder().totalCount(100).totalPages(10).build();
        assertEquals(100, p.getTotalCount());
        assertEquals(10, p.getTotalPages());
    }

    @Test
    void pageCountResponseZero() {
        var p = PageCountResponse.builder().totalCount(0).totalPages(0).build();
        assertEquals(0, p.getTotalCount());
        assertEquals(0, p.getTotalPages());
    }

    @Test
    void pageCountResponseLarge() {
        var p = PageCountResponse.builder().totalCount(Long.MAX_VALUE).totalPages(Integer.MAX_VALUE).build();
        assertEquals(Long.MAX_VALUE, p.getTotalCount());
        assertEquals(Integer.MAX_VALUE, p.getTotalPages());
    }

    @Test
    void okWithInteger() {
        ApiResponse<Integer> r = ApiResponse.ok(42);
        assertEquals(0, r.getCode());
        assertEquals("Ok", r.getMessage());
        assertEquals(42, r.getData());
    }

    @Test
    void okWithCustomObject() {
        record Custom(String name, int value) {}
        var r = ApiResponse.ok(new Custom("test", 1));
        assertEquals("test", r.getData().name());
        assertEquals(1, r.getData().value());
    }

    @Test
    void okWithNestedGenerics() {
        var r = ApiResponse.ok(Map.of("key", List.of(1, 2)));
        assertEquals(List.of(1, 2), r.getData().get("key"));
    }

    @Test
    void errorWithCodeZero() {
        var r = ApiResponse.error(0, "zero code");
        assertEquals(0, r.getCode());
    }

    @Test
    void errorWithPositiveCode() {
        var r = ApiResponse.error(10, "positive");
        assertEquals(10, r.getCode());
    }

    @Test
    void errorWithNegativeCode() {
        var r = ApiResponse.error(-10, "negative");
        assertEquals(-10, r.getCode());
    }

    @Test
    void errorWithCodeMaxValue() {
        var r = ApiResponse.error(Integer.MAX_VALUE, "max");
        assertEquals(Integer.MAX_VALUE, r.getCode());
    }

    @Test
    void errorWithCodeMinValue() {
        var r = ApiResponse.error(Integer.MIN_VALUE, "min");
        assertEquals(Integer.MIN_VALUE, r.getCode());
    }

    @Test
    void errorWithNullMessage() {
        var r = ApiResponse.error(-1, null);
        assertNull(r.getMessage());
    }

    @Test
    void errorWithEmptyMessage() {
        var r = ApiResponse.error(-1, "");
        assertEquals("", r.getMessage());
    }

    @Test
    void builderWithAllNulls() {
        var r = ApiResponse.<String>builder().build();
        assertEquals(0, r.getCode());
        assertNull(r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void builderWithPartialData() {
        var r = ApiResponse.<Integer>builder().code(5).build();
        assertEquals(5, r.getCode());
        assertNull(r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void builderWithOnlyCode() {
        var r = ApiResponse.<String>builder().code(100).build();
        assertEquals(100, r.getCode());
        assertNull(r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void builderWithOnlyMessage() {
        var r = ApiResponse.<String>builder().message("only message").build();
        assertEquals(0, r.getCode());
        assertEquals("only message", r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void setNullAfterBuilder() {
        var r = ApiResponse.ok("data");
        r.setData(null);
        assertNull(r.getData());
    }

    @Test
    void setAfterFullConstructor() {
        var r = new ApiResponse<String>(1, "initial", "data");
        r.setCode(2);
        r.setMessage("updated");
        r.setData("new");
        assertEquals(2, r.getCode());
        assertEquals("updated", r.getMessage());
        assertEquals("new", r.getData());
    }

    @Test
    void pageCountZeroTotalNonZeroPages() {
        var p = PageCountResponse.builder().totalCount(0).totalPages(5).build();
        assertEquals(0, p.getTotalCount());
        assertEquals(5, p.getTotalPages());
    }

    @Test
    void pageCountNonZeroTotalZeroPages() {
        var p = PageCountResponse.builder().totalCount(100).totalPages(0).build();
        assertEquals(100, p.getTotalCount());
        assertEquals(0, p.getTotalPages());
    }

    @Test
    void pageCountNegativeCount() {
        var p = PageCountResponse.builder().totalCount(-1).totalPages(1).build();
        assertEquals(-1, p.getTotalCount());
        assertEquals(1, p.getTotalPages());
    }

    @Test
    void pageCountNullBuilder() {
        var p = PageCountResponse.builder().build();
        assertEquals(0, p.getTotalCount());
        assertEquals(0, p.getTotalPages());
    }

    @Test
    void veryLongMessage() {
        String longMsg = "a".repeat(10000);
        var r = ApiResponse.error(-1, longMsg);
        assertEquals(longMsg, r.getMessage());
    }

    @Test
    void unicodeMessage() {
        String unicode = "日本語 español العربية ✓";
        var r = ApiResponse.error(-1, unicode);
        assertEquals(unicode, r.getMessage());
    }

    @Test
    void toStringNotNull() {
        var r = ApiResponse.ok("hello");
        assertNotNull(r.toString());
    }

    @Test
    void dataNullExplicitly() {
        var r = ApiResponse.<String>builder().code(0).message("test").data(null).build();
        assertNull(r.getData());
        assertEquals(0, r.getCode());
        assertEquals("test", r.getMessage());
    }

    @Test
    void dataEmptyList() {
        var r = ApiResponse.ok(List.of());
        assertNotNull(r.getData());
        assertTrue(r.getData().isEmpty());
    }

    @Test
    void dataMultipleItems() {
        var r = ApiResponse.ok(List.of(1, 2, 3));
        assertEquals(3, r.getData().size());
        assertEquals(List.of(1, 2, 3), r.getData());
    }

    @Test
    void equalsAndHashCode() {
        var r1 = ApiResponse.ok("a");
        var r2 = ApiResponse.ok("a");
        assertNotEquals(r1, r2);
        assertEquals(r1, r1);
    }
}
