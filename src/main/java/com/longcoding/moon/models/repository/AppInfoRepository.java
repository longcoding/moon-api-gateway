package com.longcoding.moon.models.repository;


import com.longcoding.moon.models.ehcache.AppInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppInfoRepository extends JpaRepository<AppInfo, Integer> {
}
