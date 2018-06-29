package com.example.camelrxtest;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
class CustomProcess {
    private final String processId;
    private State state;

    public CustomProcess(String processId) {
        this.processId = processId;
        state = State.NEW;
    }

    public CustomProcess updateProcess() {
        this.state = State.UPDATED;
        return this;
    }

    public CustomProcess endProcess() {
        state = State.FINISHED;
        return this;
    }

    enum State {
        NEW,
        UPDATED,
        FINISHED
    }
}
