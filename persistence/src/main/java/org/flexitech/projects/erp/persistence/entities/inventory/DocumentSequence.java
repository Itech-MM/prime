package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_DOC_SEQUENCE_TBL,
        uniqueConstraints = @UniqueConstraint(columnNames = {"doc_type", "doc_year"}))
@Getter
@Setter
public class DocumentSequence extends BasedEntity {

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "doc_year")
    private Integer docYear;

    private String prefix;

    @Column(name = "last_number")
    private Long lastNumber = 0L;
}