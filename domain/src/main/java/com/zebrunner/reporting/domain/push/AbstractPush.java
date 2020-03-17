package com.zebrunner.reporting.domain.push;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AbstractPush {

    private Type type;
    private String uid;

    public AbstractPush(Type type) {
        this.type = type;
        this.uid = UUID.randomUUID().toString();
    }

    public enum Type {
        TEST_RUN("/topic/%s.testRuns"),
        TEST("/topic/%s.testRuns.%s.tests"),
        TEST_RUN_STATISTICS("/topic/%s.statistics"),
        LAUNCHER("/topic/%s.launchers"),
        LAUNCHER_RUN("/topic/%s.launcherRuns");

        private final String websocketPathTemplate;

        Type(String websocketPathTemplate) {
            this.websocketPathTemplate = websocketPathTemplate;
        }

        public String getWebsocketPathTemplate() {
            return websocketPathTemplate;
        }

        public String buildWebsocketPath(Object... parameters) {
            return String.format(getWebsocketPathTemplate(), parameters);
        }

    }

}
