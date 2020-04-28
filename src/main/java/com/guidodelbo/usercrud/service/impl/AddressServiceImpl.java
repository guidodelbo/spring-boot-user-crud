package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.io.entity.AddressEntity;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import com.guidodelbo.usercrud.io.repository.AddressRepository;
import com.guidodelbo.usercrud.io.repository.UserRepository;
import com.guidodelbo.usercrud.service.AddressService;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.sun.xml.bind.v2.schemagen.episode.SchemaBindings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAddresses(String userId) {

        List<AddressDto> returnValue = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(userId);
        ModelMapper modelMapper = new ModelMapper();

        if(userEntity == null) {
            return returnValue;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        for (AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String userId, String addressId) {

        AddressDto returnValue = new AddressDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null)
            return returnValue;

        Iterable<AddressEntity> addressEntity = addressRepository.findAllByUserDetails(userEntity);

        for(AddressEntity address : addressEntity) {

            if(address.getAddressId().equals(userId)) {
                return new ModelMapper().map(address, AddressDto.class);
            }
        }

        return returnValue;
    }
}
