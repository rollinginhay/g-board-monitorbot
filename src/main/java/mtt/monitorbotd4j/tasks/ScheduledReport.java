package mtt.monitorbotd4j.tasks;

import com.github.benmanes.caffeine.cache.Cache;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.RestClient;
import discord4j.rest.entity.RestChannel;
import lombok.RequiredArgsConstructor;
import mtt.monitorbotd4j.entities.api.Post;
import mtt.monitorbotd4j.entities.api.Thread;
import mtt.monitorbotd4j.entities.api.ThreadOverview;
import mtt.monitorbotd4j.entities.processed.FilteredThread;
import mtt.monitorbotd4j.requests.ThreadReq;
import mtt.monitorbotd4j.requests.ThreadsOverviewReq;
import mtt.monitorbotd4j.utils.Logging;
import mtt.monitorbotd4j.utils.cacheManagement.Evictor;
import mtt.monitorbotd4j.utils.embedBuilders.PostEmbedBuilder;
import mtt.monitorbotd4j.utils.filters.ThreadFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class ScheduledReport implements Logging {
    @Value("${bot.config.thread-rep-threshold}")
    private Integer THREAD_REP_THRESHOLD;
    @Value("${bot.config.post-rep-threshold}")
    private Integer POST_REP_THRESHOLD;
    @Value("${bot.config.report-channel}")
    private String REPORT_CHANNEL;

    private final RestClient restClient;
    private final PostEmbedBuilder postEmbedBuilder;
    private final Cache<Integer, FilteredThread> cache;
    private final Evictor evictor;


    //request and process threads
    //send embeds to bot
    @Scheduled(fixedDelayString = "${bot.config.schedule-delay}", initialDelay = 60000)
    private void execute() {
        LOGGER.info("Scheduled reporting triggered");
        Mono.fromCallable(() -> {
            //get threads and filter
            List<EmbedCreateSpec> embeds = new ArrayList<>();
            Map<Integer, FilteredThread> filteredThreadMap = new HashMap<>();
            try {
                Map<Integer, ThreadOverview> threadOverviewMap = ThreadsOverviewReq.getThreads();
                Map<Integer, mtt.monitorbotd4j.entities.api.Thread> threadMap = new HashMap<>();

                for (Integer threadId : threadOverviewMap.keySet()) {
                    threadMap.put(threadId, ThreadReq.getThread(threadId));
                }

                for (Map.Entry<Integer, Thread> threadEntry : threadMap.entrySet()) {
                    List<Post> postsInThread = threadEntry.getValue().getPosts();
                    Post op = postsInThread.get(0);
                    List<Post> normalPosts = postsInThread.size() > 1 ? postsInThread.subList(1, postsInThread.size() - 1) : new ArrayList<>();
                    Map<Integer, Post> filteredNormalPosts = ThreadFilter.filterPosts(normalPosts, POST_REP_THRESHOLD);

                    FilteredThread filteredThread = new FilteredThread();
                    filteredThread.setNo(threadEntry.getKey());
                    filteredThread.setIsPopular(op.getReplies() >= THREAD_REP_THRESHOLD);
                    filteredThread.setOp(op);
                    filteredThread.setPopularPosts(filteredNormalPosts);

                    filteredThreadMap.put(threadEntry.getKey(), filteredThread);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            for (FilteredThread filteredThread : filteredThreadMap.values()) {
                //skip if present in cache and already sent
                Boolean threadIsSent = false;
                if (cache.getIfPresent(filteredThread.getNo()) != null && cache.getIfPresent(filteredThread.getNo()).getIsSent()) {
                    threadIsSent = true;
                }
                if (filteredThread.getIsPopular() && !threadIsSent) {
                    EmbedCreateSpec opEmbed = postEmbedBuilder.buildPopularPost(filteredThread.getOp(), filteredThread.getNo(), true);
                    embeds.add(opEmbed);
                }

                filteredThread.getPopularPosts().forEach((no, post) -> {
                    Boolean postIsSent = false;
                    if (cache.getIfPresent(filteredThread.getNo()) != null && cache.getIfPresent(filteredThread.getNo()).getPopularPosts().get(no) != null && cache.getIfPresent(filteredThread.getNo()).getPopularPosts().get(no).getIsSent()) {
                        postIsSent = true;
                    }
                    if (!postIsSent) {
                        EmbedCreateSpec postEmbed = postEmbedBuilder.buildPopularPost(post, filteredThread.getNo(), false);
                        embeds.add(postEmbed);
                    }
                });
            }
            //toggle isSent and cache
            filteredThreadMap.forEach((no, filteredThread) -> {
                if (filteredThread.getIsPopular()) {
                    filteredThread.setIsSent(true); //only toggle isSent if popular thread
                }
                filteredThread.getPopularPosts().forEach((postNo, post) -> {
                    post.setIsSent(true);
                });
                cache.put(no, filteredThread);
            });

            //evict non-active threads
            evictor.evictIfNotIn(filteredThreadMap.keySet());

            return embeds;
        }).flatMap(embeds -> Mono.fromRunnable(() -> {
            RestChannel channel = restClient.getChannelById(Snowflake.of(REPORT_CHANNEL));
            for (EmbedCreateSpec embed : embeds) {
                channel.createMessage(embed.asRequest()).subscribe();
            }
        })).subscribe();
    }
}
