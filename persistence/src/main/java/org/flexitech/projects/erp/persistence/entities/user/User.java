package org.flexitech.projects.erp.persistence.entities.user;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.USER_TBL)
@Getter
@Setter
public class User extends BasedEntity {
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;
    private Integer status;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
