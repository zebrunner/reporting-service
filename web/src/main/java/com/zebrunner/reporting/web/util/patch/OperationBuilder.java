package com.zebrunner.reporting.web.util.patch;

import com.zebrunner.reporting.service.exception.IllegalOperationException;

import java.util.function.Function;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.ILLEGAL_ATTRIBUTE_VALUE;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.UNSUPPORTED_PATCH_OPERATION;

public class OperationBuilder<R, P> {

    private static final String ERR_MSG_INVALID_PATCH_OPERATION = "Specified modification operation is not supported";
    private static final String ERR_MSG_INVALID_PATCH_VALUE = "Specified modification operation value is not supported";

    private PatchDecorator<R, P> decorator;
    private Enum<?> operation;

    public OperationBuilder(PatchDecorator<R, P> decorator) {
        this.decorator = decorator;
    }

    public <E extends Enum<E>> WhenBuilder operation(Class<E> operationClass) {
        this.operation = retrieveOperation(operationClass);
        return new WhenBuilder(this);
    }

    private <E extends Enum<E>> E retrieveOperation(Class<E> operationClass) {
        try {
            return Enum.valueOf(operationClass, decorator.getDescriptor().getOperation());
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException(UNSUPPORTED_PATCH_OPERATION, ERR_MSG_INVALID_PATCH_OPERATION);
        }
    }

    public class WhenBuilder {

        private OperationBuilder<R, P> operationBuilder;
        private Enum<?> whenEnum;

        public WhenBuilder(OperationBuilder<R, P> operationBuilder) {
            this.operationBuilder = operationBuilder;
        }

        public <E extends Enum<E>> ThenBuilder when(E whenEnum) {
            this.whenEnum = whenEnum;
            return new ThenBuilder(this);
        }

        public Executor after() {
            return new Executor();
        }

        public class ThenBuilder {

            private WhenBuilder whenBuilder;
            private Function<P, R> thenFunc;
            private Function<String, P> typeConverter;

            public ThenBuilder(WhenBuilder whenBuilder) {
                this.whenBuilder = whenBuilder;
            }

            public ThenBuilder withParameter(Function<String, P> typeConverter) {
                this.typeConverter = typeConverter;
                return this;
            }

            public WhenBuilder then(Function<P, R> thenFunc) {
                this.thenFunc = thenFunc;
                this.whenBuilder.operationBuilder.decorator.getOperationActions().put(this.whenBuilder.whenEnum, this);
                return this.whenBuilder;
            }

            private P castParameter() {
                P parameter;
                try {
                    parameter = typeConverter.apply(this.whenBuilder.operationBuilder.decorator.getDescriptor().getValue());
                } catch (ClassCastException | IllegalArgumentException e) {
                    throw new IllegalOperationException(ILLEGAL_ATTRIBUTE_VALUE, ERR_MSG_INVALID_PATCH_VALUE);
                }
                return parameter;
            }
        }

        public class Executor {

            public R decorate() {
                boolean operationSupported = operationBuilder.decorator.getOperationActions().containsKey(operation);
                if (!operationSupported) {
                    throw new IllegalOperationException(UNSUPPORTED_PATCH_OPERATION, ERR_MSG_INVALID_PATCH_OPERATION);
                }
                ThenBuilder operationSupplier = operationBuilder.decorator.getOperationActions().get(operation);
                P parameter = operationSupplier.castParameter();
                return operationSupplier.thenFunc.apply(parameter);
            }
        }
    }
}
