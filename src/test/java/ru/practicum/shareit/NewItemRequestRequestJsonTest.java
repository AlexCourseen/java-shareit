package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NewItemRequestRequestJsonTest {
    private final JacksonTester<NewItemRequestRequest> json;

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\n" +
                "  \"description\": \"Описание запроса\" \n" +
                "}";

        NewItemRequestRequest dto = json.parseObject(jsonContent);

        assertThat(dto.getDescription()).isEqualTo("Описание запроса");
    }
}
