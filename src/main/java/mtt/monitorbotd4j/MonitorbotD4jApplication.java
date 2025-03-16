package mtt.monitorbotd4j;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import io.github.cdimascio.dotenv.Dotenv;
import mtt.monitorbotd4j.entities.processed.FilteredThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

/**
 * Define beans for Discord gateway and rest clients
 */
@SpringBootApplication
public class MonitorbotD4jApplication {
    @Value("${bot.config.token}")
    private String BOT_TOKEN;

    public static void main(String[] args) {
        SpringApplication.run(MonitorbotD4jApplication.class, args);
    }

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder
                .create(BOT_TOKEN)
                .build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.playing("/g/ stalking")))
                .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.GUILD_MESSAGES)))
                .login()
                .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }

    @Bean
    public Cache<Integer, FilteredThread> cache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.DAYS)
                .maximumSize(10_000)
                .build();
    }
}
