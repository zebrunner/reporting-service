package com.zebrunner.reporting.domain.push;

import com.zebrunner.reporting.domain.db.Test;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPush extends AbstractPush {

    private Test test;

    public TestPush(Test test) {
        super(Type.TEST);
        this.test = test;
        if (test.getMessage() != null && !test.getMessage().isEmpty()) {
            // To improve performance on JS side
            String message = test.getMessage();
            message = message.length() > 255 ? message.substring(0, 255) : message;
            this.test.setMessage(message + " ...");
        }
    }

}
