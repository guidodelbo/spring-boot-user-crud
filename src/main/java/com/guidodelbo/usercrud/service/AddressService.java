package com.guidodelbo.usercrud.service;

import com.guidodelbo.usercrud.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String userId, String addressId);
}
