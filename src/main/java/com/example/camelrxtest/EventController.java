package com.example.camelrxtest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequiredArgsConstructor
class EventController {

    private final EventSender eventSender;
    private final EventHandler eventHandler;

    @PostMapping("/events/{id}")
    DeferredResult<ResponseEntity> addProcess(@PathVariable String id) {
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        eventSender.sendEvent(new ProcessCreatedEvent(id))
                   .subscribe(event -> result.setResult(new ResponseEntity(HttpStatus.NO_CONTENT)), result::setErrorResult);
        return result;
    }

    @PostMapping("/events/{id}/updates")
    DeferredResult<ResponseEntity> updateProcess(@PathVariable String id) {
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        eventSender.sendEvent(new ProcessUpdatedEvent(id))
                   .subscribe(event -> result.setResult(new ResponseEntity(HttpStatus.NO_CONTENT)), result::setErrorResult);
        return result;

    }

    @PostMapping("/events/{id}/ends")
    DeferredResult<ResponseEntity> endProcess(@PathVariable String id) {
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        eventSender.sendEvent(new PrecessEndedEvent(id))
                   .subscribe(event -> result.setResult(new ResponseEntity(HttpStatus.NO_CONTENT)), result::setErrorResult);
        return result;

    }

    @GetMapping("/processes")
    List<CustomProcess> getAllprocesses() {
        return eventHandler.getAllProcesses();
    }
}
