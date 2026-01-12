package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    public Address getOrCreate(Address address) {

        return addressRepository
                .findByCountryAndCityAndStreetAndHouseAndFlat(
                        address.getCountry(),
                        address.getCity(),
                        address.getStreet(),
                        address.getHouse(),
                        address.getFlat()
                )
                .orElseGet(() -> addressRepository.save(
                        Address.builder()
                                .country(address.getCountry())
                                .city(address.getCity())
                                .street(address.getStreet())
                                .house(address.getHouse())
                                .flat(address.getFlat())
                                .build()
                ));
    }
}
