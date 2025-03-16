package mtt.monitorbotd4j.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import mtt.monitorbotd4j.interactions.commands.SlashCommand;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

/**
 * Find and calls associated handlers when client receives command event
 */
@Component
public class SlashCommandListener {
    private final Collection<SlashCommand> commands;
    private Logger LOGGER = LoggerFactory.getLogger(SlashCommandListener.class);

    public SlashCommandListener(List<SlashCommand> commands, GatewayDiscordClient client) {
        this.commands = commands;

        client.on(ChatInputInteractionEvent.class, this::handle).subscribe();
    }

    public Publisher<?> handle(ChatInputInteractionEvent event) {
        LOGGER.info("Detected command /" + event.getCommandName());
        //converts list to iterable flux
        return Flux.fromIterable(commands)
                //find matching command
                .filter(command -> command.getName().equals(event.getCommandName()))
                //get matching command
                .next()
                //let class handle command logic
                .flatMap(command -> command.handle(event));
    }
}
