package org.flexitech.projects.erp.services.system_setting;

import java.util.List;

import org.flexitech.projects.erp.commons.exceptions.SystemSettingNotFoundException;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.erp.dto.system_setting.SystemSettingSearchDTO;

public interface SystemSettingService {
	SystemSettingDTO manageSystemSetting(SystemSettingDTO dto) throws SystemSettingNotFoundException;
	SearchResultDTO<SystemSettingDTO> searchSystemSetting(SystemSettingSearchDTO searchDTO);
	
	List<SystemSettingDTO> batchUpdateSystemSettings(List<SystemSettingDTO> settings) throws SystemSettingNotFoundException;
	
	SystemSettingDTO getByCode(String code)throws SystemSettingNotFoundException;
}
