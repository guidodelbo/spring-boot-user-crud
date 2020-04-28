package com.guidodelbo.usercrud.io.repository;

import com.guidodelbo.usercrud.io.entity.AddressEntity;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
}
