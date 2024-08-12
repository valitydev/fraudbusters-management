package dev.vality.fraudbusters.management.config.properties;

import io.opentelemetry.sdk.resources.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "otel")
public class OtelProperties {

    private String resource;

}
