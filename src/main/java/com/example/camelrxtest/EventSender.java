package com.example.camelrxtest;

import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.stereotype.Component;

import static com.example.camelrxtest.RouteConfiguration.*;

@Component
@Slf4j
@RequiredArgsConstructor
class EventSender {

    private final FluentProducerTemplate template;

    Flowable<ProcessCreatedEvent> sendEvent(ProcessCreatedEvent processCreatedEvent) {
        return Flowable.just(processCreatedEvent)
                       .doOnNext(x -> log.info("Sending ProcessCreatedEvent: {}", x))
                       .doOnNext(processCreatedEvent1 -> template.withBody(processCreatedEvent1)
                                                                 .to(SEDA_PROCESS_CREATED)
                                                                 .send());
    }

    Flowable<ProcessUpdatedEvent> sendEvent(ProcessUpdatedEvent processUpdatedEvent) {
        return Flowable.just(processUpdatedEvent)
                       .doOnNext(x -> log.info("Sending ProcessUpdatedEvent: {}", x))
                       .doOnNext(processCreatedEvent1 -> template.withBody(processCreatedEvent1)
                                                                 .to(SEDA_PROCESS_UPDATED)
                                                                 .send());
    }

    Flowable<PrecessEndedEvent> sendEvent(PrecessEndedEvent precessEndedEvent) {
        return Flowable.just(precessEndedEvent)
                       .doOnNext(x -> log.info("Sending PrecessEndedEvent: {}", x))
                       .doOnNext(processCreatedEvent1 -> template.withBody(processCreatedEvent1)
                                                                 .to(SEDA_PROCESS_ENDED)
                                                                 .send());
    }
}
