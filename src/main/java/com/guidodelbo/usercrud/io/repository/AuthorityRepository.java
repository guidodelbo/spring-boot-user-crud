package com.guidodelbo.usercrud.io.repository;

import com.guidodelbo.usercrud.io.entity.AuthorityEntity;
import com.guidodelbo.usercrud.io.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {

    AuthorityEntity findByName(String name);
}
