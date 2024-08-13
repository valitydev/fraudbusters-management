package dev.vality.fraudbusters.management.config;

import dev.vality.fraudbusters.management.config.properties.OtelProperties;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OtelConfig {

    private final OtelProperties otelProperties;

    @Bean
    public OpenTelemetry openTelemetryConfig() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                        .setEndpoint(otelProperties.getResource())
                        .build()).build())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();
        registerGlobalOpenTelemetry(openTelemetrySdk);
        return openTelemetrySdk;
    }

    private static void registerGlobalOpenTelemetry(OpenTelemetry openTelemetry) {
        try {
            GlobalOpenTelemetry.set(openTelemetry);
        } catch (Exception e) {
            log.warn("please initialize the ObservabilitySdk before starting the application");
            // will throw an exception if it was already set - problem is that we cannot check if was set
            // by a third-party library before
            GlobalOpenTelemetry.resetForTest();
            try {
                GlobalOpenTelemetry.set(openTelemetry);
            } catch (Exception ex) {
                log.warn("unable to set GlobalOpenTelemetry", ex);
                //now we give up - must be a race condition
            }
        }
    }
}