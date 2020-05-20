package com.zebrunner.reporting.web.util.patch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchDecorator<R, E extends Enum<E>> {

    private PatchDescriptor descriptor;
    private Enum<?> operation;

    private PatchDecorator() {
    }

    public OperationBuilder<R, E> descriptor(PatchDescriptor descriptor) {
        PatchDecorator<R, E> decorator = new PatchDecorator<>();
        decorator.setDescriptor(descriptor);
        return new OperationBuilder<>(decorator);
    }

    public static <R, E extends Enum<E>> PatchDecorator<R, E> instance() {
        return new PatchDecorator<>();
    }
}

