package dev.vality.fraudbusters.management.service;

import dev.vality.damsel.fraudbusters.Command;
import dev.vality.fraudbusters.management.converter.TemplateModelToCommandConverter;
import dev.vality.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TemplateCommandService {

    public static final String EMPTY_STRING = "";

    private final CommandSender commandSender;
    private final String topic;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getTemplate().getId();
        return commandSender.send(topic, command, key);
    }

    public Command createTemplateCommandById(String id) {
        return templateModelToCommandConverter.convert(TemplateModel.builder()
                .id(id)
                .template(EMPTY_STRING)
                .build());
    }

}
