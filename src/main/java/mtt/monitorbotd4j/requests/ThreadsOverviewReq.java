package mtt.monitorbotd4j.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mtt.monitorbotd4j.entities.api.PageOverview;
import mtt.monitorbotd4j.entities.api.ThreadOverview;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadsOverviewReq implements Domains {
    /**
     * Get threads from threads.json
     *
     * @return map of overview of threads
     */
    public static Map<Integer, ThreadOverview> getThreads() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/%s/threads.json", JSON_DOMAIN, BOARD);

        ObjectMapper mapper = new ObjectMapper();
        String rawJson = restTemplate.getForObject(url, String.class);
        List<PageOverview> pages = mapper.readValue(rawJson, new TypeReference<List<PageOverview>>() {
        });

        Map<Integer, ThreadOverview> threadOverviewMap = new HashMap<>();

        for (PageOverview page : pages) {
            for (ThreadOverview threadOverview : page.getThreads()) {
                threadOverviewMap.put(threadOverview.getNo(), threadOverview);
            }
        }
        return threadOverviewMap;
    }

}
