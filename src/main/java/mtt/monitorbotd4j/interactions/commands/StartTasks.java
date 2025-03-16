package mtt.monitorbotd4j.interactions.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.AllArgsConstructor;
import mtt.monitorbotd4j.tasks.ScheduledReport;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class StartTasks implements SlashCommand {
    private static final String SCHEDULED_TASKS = "scheduledTasks";

    private ScheduledAnnotationBeanPostProcessor postProcessor;

    private ScheduledReport schedulerConfig;

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply("Auto-update ON").then(
                Mono.fromRunnable(() -> {
                    startTask();
                })
        );
    }

    private void startTask() {
        postProcessor.postProcessAfterInitialization(schedulerConfig, SCHEDULED_TASKS);
    }

    private void stopTask() {
        postProcessor.postProcessBeforeDestruction(schedulerConfig, SCHEDULED_TASKS);
    }
}
