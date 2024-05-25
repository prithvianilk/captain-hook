package com.prithvianilk.captainhook.repository;

import com.prithvianilk.captainhook.entity.EventTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventTypeEntity, String> {
}
