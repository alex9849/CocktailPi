package net.alex9849.cocktailpi.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringUtilityTest {

    @Test
    void getNullPropertyNamesFindsUnsetProperties() {
        TestBean bean = new TestBean();
        bean.setName("Set");

        Set<String> nullProps = Arrays.stream(SpringUtility.getNullPropertyNames(bean))
                .collect(Collectors.toSet());

        assertEquals(Set.of("count", "description"), nullProps);
    }

    @Test
    void normalizeRemovesAccentsAndLowercases() {
        String normalized = SpringUtility.normalize("Cr\u00E8me Br\u00FBl\u00E9e");

        assertEquals("creme brulee", normalized);
    }

    @Test
    void loadFromStreamIgnoresUnknownFields() throws IOException {
        String json = "[{\"name\":\"one\"},{\"name\":\"two\",\"ignored\":123}]";
        ByteArrayInputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        List<TestDto> result = SpringUtility.loadFromStream(stream, TestDto.class);

        assertEquals(2, result.size());
        assertEquals("one", result.get(0).getName());
        assertEquals("two", result.get(1).getName());
    }

    private static final class TestBean {
        private String name;
        private Integer count;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    private static final class TestDto {
        private String name;

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }
}
