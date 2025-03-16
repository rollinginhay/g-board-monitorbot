package mtt.monitorbotd4j.utils.cacheManagement;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.AllArgsConstructor;
import mtt.monitorbotd4j.entities.processed.FilteredThread;
import org.springframework.stereotype.Component;

import java.util.Set;

@AllArgsConstructor
@Component
public class Evictor {
    Cache<Integer, FilteredThread> cache;

    public void evictIfNotIn(Set<Integer> presentThreads) {
        cache.asMap().keySet().removeIf(thread -> !presentThreads.contains(thread));
    }
}
