package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto getItemRequest(long id);

    Collection<ItemRequestDto> getItemRequests(long userId);

    Collection<ItemRequestDto> getAllItemRequests();

    ItemRequestDto createItemRequest(NewItemRequestRequest itemRequest, long userId);

}
