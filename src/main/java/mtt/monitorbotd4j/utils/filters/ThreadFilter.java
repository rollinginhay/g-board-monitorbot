package mtt.monitorbotd4j.utils.filters;


import mtt.monitorbotd4j.entities.api.Post;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThreadFilter {

    /**
     * Totals up the replies each post (EXCLUDING OP) of a thread
     *
     * @param list      a list of posts in a thread (IMPORTANT: ONLY PASS IN NON-OP POSTS)
     * @param threshold num of reps post must at least have
     * @returns a map of posts filtered by reps
     */
    public static Map<Integer, Post> filterPosts(List<Post> list, Integer threshold) {
        Map<Integer, Post> posts;
        Map<Integer, Integer> repCounts;

        //transform list to map of posts
        posts = list.stream().collect(Collectors.toMap(Post::getNo, p -> p));

        //reps count for each post
        repCounts = posts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getReplies() == null ? 0 : e.getValue().getReplies()));

        //Map through posts, increment rep counts
        for (Post post : posts.values()) {
            post.getBacklinks().stream().filter(l -> repCounts.get(l) != null).forEach(l -> repCounts.put(l, repCounts.get(l) + 1));
        }

        //filter posts with threshold
        Map<Integer, Integer> filteredRepCounts = repCounts.entrySet().stream().filter(p -> p.getValue() >= threshold).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //filter posts
        Map<Integer, Post> filteredPosts = posts.entrySet().stream().filter(e -> filteredRepCounts.keySet().contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (
                Map.Entry<Integer, Post> entry : filteredPosts.entrySet()) {
            Post post = entry.getValue();
            post.setReplies(filteredRepCounts.get(entry.getKey()));
        }

//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            System.out.println(mapper.writeValueAsString(filteredRepCounts));
//            System.out.println(mapper.writeValueAsString(filteredPosts));
//        } catch (
//                Exception e) {
//            throw new RuntimeException(e);
//        }
        return filteredPosts;
    }
}
