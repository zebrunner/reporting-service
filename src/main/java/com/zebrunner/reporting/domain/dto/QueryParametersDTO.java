package com.zebrunner.reporting.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class QueryParametersDTO implements Serializable {

    private static final long serialVersionUID = 5765973760872185602L;

    @NotNull
    @Min(1)
    private Long templateId;

    private Map<String, Object> paramsConfig;

}
