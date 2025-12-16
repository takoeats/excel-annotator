package com.junho.excel.style.rule;

import com.junho.excel.exception.ExcelExporterException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CellContextTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void acquire_firstCall_returnsInstance() {
        CellContext context = CellContext.acquire();
        assertNotNull(context);
    }

    @Test
    void acquire_sameThread_returnsSameInstance() {
        CellContext first = CellContext.acquire();
        CellContext second = CellContext.acquire();
        assertSame(first, second);
    }

    @Test
    void acquire_differentThread_returnsDifferentInstance() throws InterruptedException {
        CellContext mainThreadContext = CellContext.acquire();

        CellContext[] otherThreadContext = new CellContext[1];
        Thread otherThread = new Thread(() -> {
            otherThreadContext[0] = CellContext.acquire();
        });
        otherThread.start();
        otherThread.join();

        assertNotSame(mainThreadContext, otherThreadContext[0]);
    }

    @Test
    void update_validData_storesCorrectly() {
        CellContext context = CellContext.acquire();
        Object cellValue = "test";
        Object rowObject = new TestDTO("John", 30);

        context.update(cellValue, rowObject, 5, 10, "name");

        assertEquals("test", context.getCellValue());
        assertEquals(rowObject, context.getRowObject());
        assertEquals(5, context.getColumnIndex());
        assertEquals(10, context.getRowIndex());
        assertEquals("name", context.getFieldName());
    }

    @Test
    void update_allFields_accessibleThroughGetters() {
        CellContext context = CellContext.acquire();

        context.update(100, new TestDTO("Alice", 25), 3, 7, "age");

        assertEquals(100, context.getCellValue());
        assertEquals(3, context.getColumnIndex());
        assertEquals(7, context.getRowIndex());
        assertEquals("age", context.getFieldName());
    }

    @Test
    void getValueAs_correctType_returnsValue() {
        CellContext context = CellContext.acquire();
        context.update("test", null, 0, 0, "field");

        String value = context.getValueAs(String.class);
        assertEquals("test", value);
    }

    @Test
    void getValueAs_wrongType_returnsNull() {
        CellContext context = CellContext.acquire();
        context.update("test", null, 0, 0, "field");

        Integer value = context.getValueAs(Integer.class);
        assertNull(value);
    }

    @Test
    void getValueAs_nullValue_returnsNull() {
        CellContext context = CellContext.acquire();
        context.update(null, null, 0, 0, "field");

        String value = context.getValueAs(String.class);
        assertNull(value);
    }

    @Test
    void getValueAs_numberToInteger_returnsValue() {
        CellContext context = CellContext.acquire();
        context.update(100, null, 0, 0, "field");

        Integer value = context.getValueAs(Integer.class);
        assertEquals(100, value);
    }

    @Test
    void getValueAs_numberToDouble_returnsValue() {
        CellContext context = CellContext.acquire();
        context.update(100.5, null, 0, 0, "field");

        Double value = context.getValueAs(Double.class);
        assertEquals(100.5, value);
    }

    @Test
    void getValueAs_stringToString_returnsValue() {
        CellContext context = CellContext.acquire();
        context.update("hello", null, 0, 0, "field");

        String value = context.getValueAs(String.class);
        assertEquals("hello", value);
    }

    @Test
    void getFieldValue_validField_returnsValue() {
        CellContext context = CellContext.acquire();
        TestDTO dto = new TestDTO("John", 30);
        context.update(null, dto, 0, 0, "name");

        Object value = context.getFieldValue("name");
        assertEquals("John", value);
    }

    @Test
    void getFieldValue_invalidField_returnsNull() {
        CellContext context = CellContext.acquire();
        TestDTO dto = new TestDTO("John", 30);
        context.update(null, dto, 0, 0, "name");

        Object value = context.getFieldValue("nonExistent");
        assertNull(value);
    }

    @Test
    void getFieldValue_nullRowObject_returnsNull() {
        CellContext context = CellContext.acquire();
        context.update(null, null, 0, 0, "name");

        Object value = context.getFieldValue("name");
        assertNull(value);
    }

    @Test
    void getFieldValue_nullFieldName_returnsNull() {
        CellContext context = CellContext.acquire();
        TestDTO dto = new TestDTO("John", 30);
        context.update(null, dto, 0, 0, "name");

        Object value = context.getFieldValue(null);
        assertNull(value);
    }

    @Test
    void getFieldValue_noGetter_returnsNull() {
        CellContext context = CellContext.acquire();
        NoGetterDTO dto = new NoGetterDTO();
        context.update(null, dto, 0, 0, "field");

        Object value = context.getFieldValue("field");
        assertNull(value);
    }

    @Test
    void getFieldValue_exceptionDuringAccess_throwsException() {
        CellContext context = CellContext.acquire();
        ExceptionThrowingDTO dto = new ExceptionThrowingDTO();
        context.update(null, dto, 0, 0, "value");

        assertThrows(ExcelExporterException.class, () -> {
            context.getFieldValue("value");
        });
    }

    @Test
    void close_afterClose_clearsValues() {
        CellContext context = CellContext.acquire();
        context.update("test", new TestDTO("John", 30), 5, 10, "name");

        context.close();

        assertNull(context.getCellValue());
        assertNull(context.getRowObject());
        assertNull(context.getFieldName());
    }

    @Test
    void close_multipleClose_noError() {
        CellContext context = CellContext.acquire();
        context.update("test", null, 0, 0, "field");

        assertDoesNotThrow(() -> {
            context.close();
            context.close();
            context.close();
        });
    }

    @Test
    void threadLocal_concurrentAccess_isolatesContexts() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicBoolean failed = new AtomicBoolean(false);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    CellContext context = CellContext.acquire();
                    context.update("value-" + threadId, null, threadId, threadId, "field-" + threadId);

                    Thread.sleep(10);

                    if (!("value-" + threadId).equals(context.getCellValue())) {
                        failed.set(true);
                    }
                    if (context.getColumnIndex() != threadId) {
                        failed.set(true);
                    }

                    context.close();
                } catch (Exception e) {
                    failed.set(true);
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();
        assertFalse(failed.get());
    }

    public static class TestDTO {
        private final String name;
        private final Integer age;

        public TestDTO(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }

    public static class NoGetterDTO {
        public String field = "value";
    }

    public static class ExceptionThrowingDTO {
        public String getValue() {
            throw new RuntimeException("Intentional exception");
        }
    }
}
