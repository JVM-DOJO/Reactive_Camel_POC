package com.example.camelrxtest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class PrecessEndedEvent implements Event {
    private final String processId;
}
