package com.crud.restapi.repository;



import com.crud.restapi.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
}
