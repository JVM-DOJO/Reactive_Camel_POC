package com.example.camelrxtest;

import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
class EventHandler {

    private final ConcurrentHashMap<String, CustomProcess> database = new ConcurrentHashMap<>();

    public Flowable<ProcessCreatedEvent> processCreatedHandler(ProcessCreatedEvent processCreatedEvent) {
        return Flowable.just(processCreatedEvent)
                       .doOnNext(c -> log.info("Processing message {}", c))
                       .map(x -> {
                           database.put(x.getProcessId(), new CustomProcess(x.getProcessId()));
                           return x;
                       })
                       .delay(200, TimeUnit.MILLISECONDS)
                       .doOnNext(x -> log.info("Event added: {}", x));
    }

    public Flowable<ProcessUpdatedEvent> processUpdatedHandler(ProcessUpdatedEvent event) {
        return Flowable.just(event)
                       .map(x -> database.get(event.getProcessId()))
                       .map(CustomProcess::updateProcess)
                       .map(p -> database.put(p.getProcessId(), p))
                       .delay(200, TimeUnit.MILLISECONDS)
                       .doOnNext(x -> log.info("Event updated: {}", x))
                       .map(x -> event);
    }

    public Flowable<PrecessEndedEvent> processEndedHandler(PrecessEndedEvent event) {
        return Flowable.just(event)
                       .map(x -> database.get(event.getProcessId()))
                       .map(CustomProcess::endProcess)
                       .delay(200, TimeUnit.MILLISECONDS)
                       .map(p -> database.put(p.getProcessId(), p))
                       .doOnNext(x -> log.info("Event ended: {}", x))
                       .map(x -> event);
    }

    public Flowable<Event> processLoggerHandler(Event event) {
        return Flowable.just(event)
                       .doOnNext(x -> log.info("Event LOGGED: {}", x));
    }

    public List<CustomProcess> getAllProcesses() {
        return new ArrayList<>(database.values());
    }

}
