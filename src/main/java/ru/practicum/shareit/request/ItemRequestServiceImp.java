package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(NewItemRequestRequest newItemRequest, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        if (newItemRequest.getDescription() == null || newItemRequest.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано");
        }
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(newItemRequest);
        itemRequest.setAuthor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getItemRequest(long requestId) {
        ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(
                itemRequestRepository.findById(requestId).orElseThrow(() ->
                        new NotFoundException("Запрос с ID: " + requestId + " не найден")));
        dto.setItems(itemsToItemRequest(requestId));
        return dto;
    }

    @Override
    public List<ItemRequestDto> getItemRequests(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        List<ItemRequestDto> itemRequests = itemRequestRepository.findByAuthorIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
        itemRequests.forEach(itemRequest ->
                itemRequest.setItems(itemsToItemRequest(itemRequest.getId())));
        return itemRequests;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        List<ItemRequestDto> itemRequests = itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
        itemRequests.forEach(itemRequest ->
                itemRequest.setItems(itemsToItemRequest(itemRequest.getId())));
        return itemRequests;
    }

    private List<ItemForRequestDto> itemsToItemRequest(long requestId) {
        return itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToItemForRequestDto)
                .toList();
    }
}
