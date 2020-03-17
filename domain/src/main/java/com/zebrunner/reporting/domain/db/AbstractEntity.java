package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {
    private static final long serialVersionUID = 6187567312503626298L;

    private Long id;
    @Transient
    private Date modifiedAt;
    @Transient
    private Date createdAt;

    public AbstractEntity(Long id) {
        this.id = id;
    }
}