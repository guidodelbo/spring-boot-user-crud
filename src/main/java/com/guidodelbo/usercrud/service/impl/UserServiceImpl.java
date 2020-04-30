package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import com.guidodelbo.usercrud.io.repository.UserRepository;
import com.guidodelbo.usercrud.service.UserService;
import com.guidodelbo.usercrud.shared.Utils;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import com.guidodelbo.usercrud.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto saveUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        List<AddressDto> addresses = user.getAddresses();

        IntStream.range(0, addresses.size()).forEach(i -> {
            AddressDto address = addresses.get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            addresses.set(i, address);
        });

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        return modelMapper.map(storedUserDetails, UserDto.class);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException("User with ID: " + userId + " was not found.");

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    // Spring uses this method to retrieve user name and password when needed
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());
    }

//    public List<UserDetailsRequestModel> saveUsers(List<UserDetailsRequestModel> users) {
//        return repository.saveAll(users);
//    }


//    public UserDetailsRequestModel getUserByDni(int dni) {
//        return repository.findByDni(dni);
//    }
//
//    public UserDetailsRequestModel getUserByName(String name) {
//        return repository.findByName(name);
//    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        // Here depends on business logic either to check or not for empty name/surname
        userEntity.setName(user.getName());
        userEntity.setSurname(user.getSurname());

        UserEntity updatedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }
    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if(page > 0)
            page -= 1;

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

            return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if(userEntity != null){
          boolean hasTokenExpired = Utils.hasTokenExpired(token);
          if(!hasTokenExpired){
              userEntity.setEmailVerificationToken(null);
              userEntity.setEmailVerificationStatus(Boolean.TRUE);
              userRepository.save(userEntity);
              returnValue = true;
          }
        }
        return returnValue;
    }
}
