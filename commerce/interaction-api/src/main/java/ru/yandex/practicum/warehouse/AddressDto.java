package ru.yandex.practicum.warehouse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
