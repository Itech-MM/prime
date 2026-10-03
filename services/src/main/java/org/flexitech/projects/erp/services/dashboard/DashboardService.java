package org.flexitech.projects.erp.services.dashboard;

import java.util.Date;

import org.flexitech.projects.erp.dto.dashboard.DashboardDTO;

public interface DashboardService {
	DashboardDTO getDashboardSummary(Date startDate, Date endDate) throws Exception;
}