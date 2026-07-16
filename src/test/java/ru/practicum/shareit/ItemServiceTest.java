package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImp;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemServiceImp itemServiceImp;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Test
    void getItems() {
        Item item1 = new Item();
        Item item2 = new Item();
        User owner1 = new User();
        User booker1 = new User();
        User booker2 = new User();
        User authorComment = new User();

        booker1.setId(1L);
        booker1.setName("Букер1");
        booker1.setEmail("booker@mail.com");
        userRepository.save(booker1);

        booker2.setId(2L);
        booker2.setName("Букер2");
        booker2.setEmail("booker2@mail.com");
        userRepository.save(booker2);

        owner1.setId(3L);
        owner1.setName("Хозяин");
        owner1.setEmail("owner@mail.com");
        userRepository.save(owner1);

        authorComment.setId(4L);
        authorComment.setName("АвторОтзыва");
        authorComment.setEmail("author@mail.com");
        userRepository.save(authorComment);

        item1.setId(1L);
        item1.setOwner(owner1);
        item1.setDescription("описание вещи1");
        item1.setName("Вещь1");
        item1.setAvailable(true);
        itemRepository.save(item1);

        item2.setId(2L);
        item2.setOwner(owner1);
        item2.setDescription("описание вещи2");
        item2.setName("Вещь2");
        item2.setAvailable(false);
        itemRepository.save(item2);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("отзыв");
        comment.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        comment.setAuthor(authorComment);
        comment.setItem(item1);
        commentRepository.save(comment);

        Booking lastBook = new Booking();
        lastBook.setId(1L);
        lastBook.setItem(item1);
        lastBook.setBooker(booker1);
        lastBook.setEndDate(LocalDateTime.now().minusDays(3).withNano(0));
        lastBook.setStartDate(LocalDateTime.now().minusDays(4).withNano(0));
        lastBook.setStatus(Status.APPROVED);
        bookingRepository.save(lastBook);

        Booking nextBook = new Booking();
        nextBook.setId(2L);
        nextBook.setItem(item1);
        nextBook.setBooker(booker2);
        nextBook.setEndDate(LocalDateTime.now().plusDays(2).withNano(0));
        nextBook.setStartDate(LocalDateTime.now().plusDays(1).withNano(0));
        nextBook.setStatus(Status.WAITING);
        bookingRepository.save(nextBook);

        List<ItemBookingDto> items = itemServiceImp.getItems(owner1.getId()).stream().toList();

        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), equalTo(item1.getId()));
        assertThat(items.get(0).getName(), equalTo(item1.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(items.get(0).getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(items.get(0).getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(items.get(0).getComments().get(0).getCreated(), equalTo(comment.getCreated()));
        assertThat(items.get(0).getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(items.get(0).getLastBooking().getId(), equalTo(lastBook.getId()));
        assertThat(items.get(0).getLastBooking().getBooker(), equalTo(lastBook.getBooker()));
        assertThat(items.get(0).getLastBooking().getItem(), equalTo(lastBook.getItem()));
        assertThat(items.get(0).getLastBooking().getStatus(), equalTo(lastBook.getStatus()));
        assertThat(items.get(0).getLastBooking().getStartDate(), equalTo(lastBook.getStartDate()));
        assertThat(items.get(0).getLastBooking().getEndDate(), equalTo(lastBook.getEndDate()));
        assertThat(items.get(0).getNextBooking().getId(), equalTo(nextBook.getId()));
        assertThat(items.get(0).getNextBooking().getBooker(), equalTo(nextBook.getBooker()));
        assertThat(items.get(0).getNextBooking().getItem(), equalTo(nextBook.getItem()));
        assertThat(items.get(0).getNextBooking().getStatus(), equalTo(nextBook.getStatus()));
        assertThat(items.get(0).getNextBooking().getStartDate(), equalTo(nextBook.getStartDate()));
        assertThat(items.get(0).getNextBooking().getEndDate(), equalTo(nextBook.getEndDate()));

        assertThat(items.get(1).getId(), equalTo(item2.getId()));
        assertThat(items.get(1).getName(), equalTo(item2.getName()));
        assertThat(items.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(items.get(1).getComments().size(), equalTo(0));
        assertThat(items.get(1).getLastBooking(), nullValue());
        assertThat(items.get(1).getNextBooking(), nullValue());
    }
}
