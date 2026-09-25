package org.flexitech.projects.erp.dto.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyPermissionsRequest {
    private Long sourceRoleId;
    private Long targetRoleId;
}