package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters.P2PServiceSrv;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class P2PFraudoConfig {

    @Bean
    public P2PServiceSrv.Iface p2pServiceSrv(@Value("${service.p2p.url}") Resource resource,
                                             @Value("${service.p2p.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(P2PServiceSrv.Iface.class);
    }

    @Bean
    public TemplateCommandService p2pTemplateCommandService(CommandSender commandSender,
                                                            @Value("${kafka.topic.fraudbusters.p2p.template}") String topic) {
        return new TemplateCommandService(commandSender, topic);
    }

    @Bean
    public GroupCommandService p2pGroupCommandService(CommandSender commandSender,
                                                      @Value("${kafka.topic.fraudbusters.p2p.group.list}") String topic) {
        return new GroupCommandService(commandSender, topic);
    }


}