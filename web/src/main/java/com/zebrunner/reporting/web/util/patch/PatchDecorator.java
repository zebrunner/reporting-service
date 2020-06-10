package com.zebrunner.reporting.web.util.patch;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PatchDecorator<R, P> {

    private PatchDescriptor descriptor;
    private Enum<?> operation;
    private Map<Enum<?>, OperationBuilder<R, P>.WhenBuilder.ThenBuilder> operationActions;

    private PatchDecorator() {
        this.operationActions = new HashMap<>();
    }

    public static <R, P> OperationBuilder<R, P> descriptor(PatchDescriptor descriptor) {
        PatchDecorator<R, P> decorator = new PatchDecorator<>();
        decorator.setDescriptor(descriptor);
        return new OperationBuilder<>(decorator);
    }
}

