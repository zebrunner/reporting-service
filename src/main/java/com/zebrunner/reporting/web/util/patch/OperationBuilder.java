package com.zebrunner.reporting.web.util.patch;

import com.zebrunner.reporting.service.exception.IllegalOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.ILLEGAL_ATTRIBUTE_VALUE;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.UNSUPPORTED_PATCH_OPERATION;

public class OperationBuilder<R, E extends Enum<E>> {

    private static final String ERR_MSG_INVALID_PATCH_OPERATION = "Specified modification operation is not supported";
    private static final String ERR_MSG_INVALID_PATCH_VALUE = "Specified modification operation value is not supported";

    private final PatchDecorator<R, E> decorator;
    private Enum<E> operation;

    private final Map<Enum<E>, WhenBuilder.ThenBuilder<?>> operationActions;

    public OperationBuilder(PatchDecorator<R, E> decorator) {
        this.decorator = decorator;
        this.operationActions = new HashMap<>();
    }

    public WhenBuilder operation(Class<E> operationClass) {
        this.operation = retrieveOperation(operationClass);
        return new WhenBuilder(this);
    }

    private E retrieveOperation(Class<E> operationClass) {
        try {
            return Enum.valueOf(operationClass, decorator.getDescriptor().getOperation());
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException(UNSUPPORTED_PATCH_OPERATION, ERR_MSG_INVALID_PATCH_OPERATION);
        }
    }

    public class WhenBuilder {

        private final OperationBuilder<R, E> operationBuilder;
        private Enum<E> whenEnum;

        public WhenBuilder(OperationBuilder<R, E> operationBuilder) {
            this.operationBuilder = operationBuilder;
        }

        public <P> ThenBuilder<P> when(E whenEnum) {
            this.whenEnum = whenEnum;
            return new ThenBuilder<>(this);
        }

        public WhenBuilder and() {
            return new WhenBuilder(operationBuilder);
        }

        public ThenBuilder<?>.Executor after() {
            return new ThenBuilder<>(this).newExecutor();
        }

        public class ThenBuilder<P> {

            private final WhenBuilder whenBuilder;
            private Function<P, R> thenFunc;
            private Function<String, P> typeConverter;

            public ThenBuilder(WhenBuilder whenBuilder) {
                this.whenBuilder = whenBuilder;
            }

            public ThenBuilder<P> withParameter(Function<String, P> typeConverter) {
                this.typeConverter = typeConverter;
                return this;
            }

            public WhenBuilder then(Function<P, R> thenFunc) {
                this.thenFunc = thenFunc;
                this.whenBuilder.operationBuilder.operationActions.put(this.whenBuilder.whenEnum, this);
                return this.whenBuilder;
            }

            @SuppressWarnings("unchecked")
            private P castParameter() {
                P parameter;
                try {
                    if (typeConverter != null) {
                        parameter = typeConverter.apply(this.whenBuilder.operationBuilder.decorator.getDescriptor().getValue());
                    } else {
                        parameter = (P) this.whenBuilder.operationBuilder.decorator.getDescriptor().getValue();
                    }
                } catch (ClassCastException | IllegalArgumentException e) {
                    throw new IllegalOperationException(ILLEGAL_ATTRIBUTE_VALUE, ERR_MSG_INVALID_PATCH_VALUE);
                }
                return parameter;
            }

            public class Executor {

                public R decorate() {
                    boolean operationSupported = operationActions.containsKey(operation);
                    if (!operationSupported) {
                        throw new IllegalOperationException(UNSUPPORTED_PATCH_OPERATION, ERR_MSG_INVALID_PATCH_OPERATION);
                    }
                    @SuppressWarnings("unchecked")
                    ThenBuilder<P> operationSupplier = (ThenBuilder<P>) operationActions.get(operation);
                    P parameter = operationSupplier.castParameter();
                    return operationSupplier.thenFunc.apply(parameter);
                }
            }

            private Executor newExecutor() {
                return new Executor();
            }
        }
    }
}
