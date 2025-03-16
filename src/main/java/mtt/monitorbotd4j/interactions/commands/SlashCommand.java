package mtt.monitorbotd4j.interactions.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Slash command contract interface
 * getName() provides command name
 * handle() houses command logic
 */
public interface SlashCommand {
    String getName();

    Mono<?> handle(ChatInputInteractionEvent event);
}
