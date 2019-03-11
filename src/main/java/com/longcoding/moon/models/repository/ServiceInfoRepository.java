package com.longcoding.moon.models.repository;

import com.longcoding.moon.models.ehcache.ServiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, String> {
}
