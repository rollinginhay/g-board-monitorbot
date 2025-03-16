package mtt.monitorbotd4j.listeners;

import com.github.benmanes.caffeine.cache.Cache;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import mtt.monitorbotd4j.entities.api.Post;
import mtt.monitorbotd4j.entities.processed.FilteredThread;
import mtt.monitorbotd4j.requests.ThreadReq;
import mtt.monitorbotd4j.utils.Logging;
import mtt.monitorbotd4j.utils.embedBuilders.PostEmbedBuilder;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Detects and embeds /g/ links
 */

@Component
public class LinkEmbedListener implements Logging {
    private final Pattern pattern = Pattern.compile("^https://boards.4chan.org/g/thread/[0-9#p]*$");
    private PostEmbedBuilder postEmbedBuilder;
    Cache<Integer, FilteredThread> cache;

    public LinkEmbedListener(GatewayDiscordClient client, PostEmbedBuilder postEmbedBuilder, Cache<Integer, FilteredThread> cache) {
        this.postEmbedBuilder = postEmbedBuilder;
        this.cache = cache;
        client.on(MessageCreateEvent.class, this::handle).subscribe();
    }

    public Publisher<?> handle(MessageCreateEvent event) {
        return Mono.fromRunnable(() -> {
            String content = event.getMessage().getContent();
            if (pattern.matcher(content).matches()) {
                LOGGER.info("Embeddable link detected: " + content);
                try {
                    URL url = new URL(content);
                    Integer threadId = Integer.parseInt(url.getPath().split("/")[3]);
                    Integer postId = url.getRef() == null ? null : Integer.parseInt(url.getRef().substring(1, url.getRef().length()));

                    EmbedCreateSpec embed;
                    if (postId == null) {
                        Post post = ThreadReq.getThread(threadId).getPosts().get(0);
                        embed = postEmbedBuilder.buildAnyPost(post, threadId, true);
                    } else {
                        Post post = ThreadReq.getThread(threadId).getPosts().stream().filter(p -> Objects.equals(p.getNo(), postId)).findFirst().get();
                        embed = postEmbedBuilder.buildAnyPost(post, threadId, false);
                    }

                    event.getMessage().getChannel().flatMap(channel -> channel.createMessage(embed)).subscribe();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
