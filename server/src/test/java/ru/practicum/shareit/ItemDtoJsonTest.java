package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemWithCommentsDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now().minusDays(2).withNano(0));
        comment.setText("текст отзыва");
        comment.setAuthorName("АвторОтзыва");

        ItemWithCommentsDto dto = new ItemWithCommentsDto();
        dto.setId(1L);
        dto.setDescription("Описание вещи");
        dto.setName("Название вещи");
        dto.setAvailable(false);
        dto.setComments(List.of(comment));

        JsonContent<ItemWithCommentsDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(comment.getCreated().toString());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(comment.getAuthorName());
    }
}