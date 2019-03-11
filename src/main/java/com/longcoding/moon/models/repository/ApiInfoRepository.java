package com.longcoding.moon.models.repository;


import com.longcoding.moon.models.ehcache.ApiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoRepository extends JpaRepository<ApiInfo, String> {
}
