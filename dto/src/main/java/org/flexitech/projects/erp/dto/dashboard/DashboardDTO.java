package org.flexitech.projects.erp.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalProducts;
    private long lowStockCount;
    private List<String> salesLabels;
    private List<BigDecimal> salesData;
    private List<String> pieLabels;
    private List<Long> pieData;
}
