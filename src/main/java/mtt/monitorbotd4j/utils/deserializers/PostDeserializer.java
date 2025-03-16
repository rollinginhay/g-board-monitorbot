package mtt.monitorbotd4j.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import mtt.monitorbotd4j.entities.api.Post;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostDeserializer extends JsonDeserializer<Post> {
    @Override
    public Post deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        Post post = new Post();
        JsonNode node = jsonParser.readValueAsTree();

        post.setNo(node.get("no").asInt());
        post.setNow(node.get("now").asText());
        post.setSub(node.get("sub") == null ? null : node.get("sub").asText());
        post.setCom(node.get("com") == null ? null : node.get("com").asText());
        post.setExt(node.get("ext") == null ? null : node.get("ext").asText());
        post.setReplies(node.get("replies") == null ? null : node.get("replies").asInt());
        post.setTime(node.get("time").asLong());
        post.setTim(node.get("tim") == null ? null : node.get("tim").asLong());
        post.setBacklinks(new ArrayList<>());
        String comText = node.get("com") == null ? null : node.get("com").asText();

        //SETTING BACKLINK
        /*
         * Quotelink variants:
         * 1. #p{postId}
         * 2. /{board}/thread/{threadId}#p{postId} (cross-thread)
         * 3. //{board}/#s={board} (cross-board)
         * ignore variants 2 and 3
         * */

        if (comText == null) return post;
        Elements eles = Jsoup.parse(comText).getElementsByClass("quotelink");

        if (eles.isEmpty()) return post;
        List<Integer> backLinks = eles.stream().map(e -> e.attr("href")).filter(text -> text.startsWith("#p")).map(text -> text.substring(2)).map(Integer::valueOf).toList();
        post.setBacklinks(backLinks);

        return post;
    }
}
