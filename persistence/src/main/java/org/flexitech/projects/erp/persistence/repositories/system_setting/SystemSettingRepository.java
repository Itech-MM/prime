package org.flexitech.projects.erp.persistence.repositories.system_setting;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.system_setting.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long>, JpaSpecificationExecutor<SystemSetting>{
	Optional<SystemSetting> findByCode(String code);
}
