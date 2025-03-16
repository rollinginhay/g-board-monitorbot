package mtt.monitorbotd4j.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mtt.monitorbotd4j.entities.api.Thread;
import org.springframework.web.client.RestTemplate;

public class ThreadReq implements Domains {
    /**
     * Get a representation of a single thread, including OP post
     *
     * @param threadId the thread id
     * @returns a map of posts in thread
     */
    public static Thread getThread(Integer threadId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/%s/thread/%s.json", JSON_DOMAIN, BOARD, threadId);

        ObjectMapper mapper = new ObjectMapper();
        String rawJson = restTemplate.getForObject(url, String.class);
        Thread thread = mapper.readValue(rawJson, new TypeReference<Thread>() {
        });

        return thread;

    }
}
