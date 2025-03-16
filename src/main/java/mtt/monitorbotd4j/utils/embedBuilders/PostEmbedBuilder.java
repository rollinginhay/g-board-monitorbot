package mtt.monitorbotd4j.utils.embedBuilders;

import discord4j.core.spec.EmbedCreateSpec;
import mtt.monitorbotd4j.entities.api.Post;
import mtt.monitorbotd4j.requests.Domains;
import mtt.monitorbotd4j.utils.Logging;
import mtt.monitorbotd4j.utils.parsers.HtmlParser;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PostEmbedBuilder implements Domains, Logging {
    public EmbedCreateSpec buildPopularPost(Post post, Integer threadId, boolean isOp) {
        String postCom = post.getCom() == null ? "" : HtmlParser.getPlainText(Jsoup.parse(post.getCom()));

        String embedTitle;
        String embedUrl;

        if (isOp) {
            String postSub = post.getSub() == null ? "" : HtmlParser.getPlainText(Jsoup.parse(post.getSub()));
            embedTitle = "Popular thread on /g/ " + String.format("#%s: %s", post.getNo(), postSub);
            embedUrl = String.format("%s/%s/thread/%s", PAGE, BOARD, threadId);
        } else {
            embedTitle = "Popular post on /g/ " + String.format("#%s", post.getNo());
            embedUrl = String.format("%s/%s/thread/%s#p%s", PAGE, BOARD, threadId, post.getNo());
        }

        // Create the embed

        return EmbedCreateSpec.builder()
                .title(embedTitle)
                .url(embedUrl)
                .description(postCom)
                .footer(post.getReplies() + " /you/s", null)
                .image(String.format("%s/%s/%s%s", MEDIA_DOMAIN, BOARD, post.getTim(), post.getExt()))
                .timestamp(Instant.ofEpochSecond(post.getTime()))
                .build();
    }

    public EmbedCreateSpec buildAnyPost(Post post, Integer threadId, boolean isOp) {
        String postCom = post.getCom() == null ? "" : HtmlParser.getPlainText(Jsoup.parse(post.getCom()));

        String embedTitle;
        String embedUrl;

        if (isOp) {
            String postSub = post.getSub() == null ? "" : HtmlParser.getPlainText(Jsoup.parse(post.getSub()));
            embedTitle = String.format("#%s: %s", post.getNo(), postSub);
            embedUrl = String.format("%s/%s/thread/%s", PAGE, BOARD, threadId);
        } else {
            embedTitle = String.format("#%s", post.getNo());
            embedUrl = String.format("%s/%s/thread/%s#p%s", PAGE, BOARD, threadId, post.getNo());
        }

        // Create the embed

        return EmbedCreateSpec.builder()
                .title(embedTitle)
                .url(embedUrl)
                .description(postCom)
                .image(String.format("%s/%s/%s%s", MEDIA_DOMAIN, BOARD, post.getTim(), post.getExt()))
                .timestamp(Instant.ofEpochSecond(post.getTime()))
                .build();
    }
}
