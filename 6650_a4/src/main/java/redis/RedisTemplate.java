package redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import io.swagger.model.SkierVertical;
import io.swagger.model.SkierVerticalResorts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class RedisTemplate {
    private static final String REDIS_IP = "52.27.122.82";

    private final RedisConnection<String, String> connection;

    public RedisTemplate() {
        RedisClient client = new RedisClient(RedisURI.create(String.format("redis://%s:6379", REDIS_IP)));
        this.connection = client.connect();
    }

    public void writeEntry(String skierId, String resortId, String seasonId, String dayId, Integer liftId) {
        String skierStatKey = String.join("/", resortId, seasonId, dayId);
        connection.sadd(skierStatKey, skierId);

        String verticalStatKey = String.join("/", skierId, resortId, seasonId, dayId);
        connection.incrby(verticalStatKey, liftId);

        String skierVerticalStatValue = String.join("/", resortId, seasonId, liftId.toString());
        connection.rpush(skierId, skierVerticalStatValue);
    }

    public Integer getUniqueSkiersCount(String resortId, String seasonId, String dayId) {
        String key = String.join("/", resortId, seasonId, dayId);
        return connection.scard(key).intValue();
    }

    public Integer getVerticalForSkier(String skierId, String resortId, String seasonId, String dayId) {
        String key = String.join("/", skierId, resortId, seasonId, dayId);
        String value = connection.get(key);
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public SkierVertical getVerticalsPerSeason(String skierId, Set<String> resorts, Set<String> seasons) {
        List<String> tuples = connection.lrange(skierId, 0, -1);
        List<SkierVerticalResorts> verticals = new ArrayList<>();
        for (String tuple : tuples) {
            String[] values = tuple.split("/");
            String resortId = values[0];
            String seasonId = values[1];
            String liftId = values[2];
            if (resorts.contains(resortId) && (seasons.isEmpty() || seasons.contains(seasonId))) {
                verticals.add(new SkierVerticalResorts().seasonID(seasonId).totalVert(Integer.parseInt(liftId) * 10));
            }
        }
        return new SkierVertical().resorts(verticals);
    }
}
