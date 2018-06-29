package com.example.camelrxtest;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.impl.ThrottlingInflightRoutePolicy;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteConfiguration {

    static final String REACTIVE_STREAMS = "reactive-streams:";
    static final String SEDA_PROCESS_CREATED = "seda:process-created?waitForTaskToComplete=Never";
    static final String SEDA_PROCESS_UPDATED = "seda:process-updated?waitForTaskToComplete=Never";
    static final String SEDA_PROCESS_ENDED = "seda:process-ended?waitForTaskToComplete=Never";
    static final String PROCESS_CREATED = "process-created";
    static final String PROCESS_UPDATED = "process-updated";
    static final String PROCESS_ENDED = "process-ended";
    static final String PROCESS_LOGGER = "process-logger";

    private final CamelReactiveStreamsService camelReactiveStreams;

    @Bean
    FluentProducerTemplate template(CamelContext camelContext) {
        return camelContext.createFluentProducerTemplate();
    }

    @Bean
    Disposable createdProcessRouteProcessor(EventHandler eventHandler) {
        Publisher<ProcessCreatedEvent> processCreatedEventPublisher = camelReactiveStreams.fromStream(PROCESS_CREATED, ProcessCreatedEvent.class);
        return Flowable.fromPublisher(processCreatedEventPublisher)
                       .flatMap(eventHandler::processCreatedHandler)
                       .subscribe();
    }

    @Bean
    Disposable updatedProcessRouteProcessor(EventHandler eventHandler) {
        Publisher<ProcessUpdatedEvent> processCreatedEventPublisher = camelReactiveStreams.fromStream(PROCESS_UPDATED, ProcessUpdatedEvent.class);
        return Flowable.fromPublisher(processCreatedEventPublisher)
                       .flatMap(eventHandler::processUpdatedHandler)
                       .subscribe();
    }

    @Bean
    Disposable endedProcessRouteProcessor(EventHandler eventHandler) {
        Publisher<PrecessEndedEvent> processCreatedEventPublisher = camelReactiveStreams.fromStream(PROCESS_ENDED, PrecessEndedEvent.class);
        return Flowable.fromPublisher(processCreatedEventPublisher)
                       .flatMap(eventHandler::processEndedHandler)
                       .subscribe();
    }

    @Bean
    Disposable loggerProcessRouteProcessor(EventHandler eventHandler) {
        Publisher<Event> processCreatedEventPublisher = camelReactiveStreams.fromStream(PROCESS_LOGGER, Event.class);
        return Flowable.fromPublisher(processCreatedEventPublisher)
                       .flatMap(eventHandler::processLoggerHandler)
                       .subscribe();
    }

    @Bean
    RouteBuilder createdProcessRouteSeda() {
        ThrottlingInflightRoutePolicy inflight = new ThrottlingInflightRoutePolicy();
        inflight.setMaxInflightExchanges(25);
        // start Camel consumer again when we are down or below 25% of max
        inflight.setResumePercentOfMax(25);
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(SEDA_PROCESS_CREATED)
                        .routePolicy(inflight)
                        .to(REACTIVE_STREAMS + PROCESS_CREATED + "?concurrentConsumers=4")
                        .to(REACTIVE_STREAMS + PROCESS_LOGGER);
            }
        };
    }

    @Bean
    RouteBuilder updatedProcessRoute(EventHandler eventHandler) {
        ThrottlingInflightRoutePolicy inflight = new ThrottlingInflightRoutePolicy();
        inflight.setMaxInflightExchanges(25);
        // start Camel consumer again when we are down or below 25% of max
        inflight.setResumePercentOfMax(25);
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(SEDA_PROCESS_UPDATED)
                        .routePolicy(inflight)
                        .to(REACTIVE_STREAMS + PROCESS_UPDATED + "?concurrentConsumers=4");
            }
        };
    }

    @Bean
    RouteBuilder endedProcessRoute(EventHandler eventHandler) {
        ThrottlingInflightRoutePolicy inflight = new ThrottlingInflightRoutePolicy();
        inflight.setMaxInflightExchanges(25);
        // start Camel consumer again when we are down or below 25% of max
        inflight.setResumePercentOfMax(25);
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(SEDA_PROCESS_ENDED)
                        .routePolicy(inflight)
                        .to(REACTIVE_STREAMS + PROCESS_ENDED + "?concurrentConsumers=4");
            }
        };
    }

}
