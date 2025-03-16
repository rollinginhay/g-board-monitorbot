package mtt.monitorbotd4j.entities.processed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import mtt.monitorbotd4j.entities.api.Post;

import java.util.Map;

/**
 * A filtered thread for popular OP and posts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilteredThread {
    private Integer no;
    private Boolean isPopular;
    private Post op;
    private Map<Integer, Post> popularPosts;
    private Boolean isSent = false; //is already sent to bot or not
}
