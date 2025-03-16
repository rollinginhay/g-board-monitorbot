package mtt.monitorbotd4j.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import mtt.monitorbotd4j.utils.deserializers.PostDeserializer;

import java.util.List;

/**
 * A post, can be either OP post or normal post
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = PostDeserializer.class)
public class Post {
    private Integer no; //post no
    private String now; //mmddyy creation timestamp
    private String sub; //thread subject
    private String com; //thread comment
    private String ext; //attachment type
    private Integer replies; //num of replies
    private Long time; //post creation timestamp
    private Long tim; //attachment creation timestamp and id
    private List<Integer> backlinks; //id of posts this post replies to
    private Boolean isSent = false;//is already sent to bot or not
}
