package com.example.camelrxtest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ProcessUpdatedEvent implements Event {
    private final String processId;
}
