package com.example.camelrxtest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ProcessCreatedEvent implements Event {
    private final String processId;
}
