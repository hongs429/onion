package com.onion.backend.infrastructure.redis;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void set(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(result, clazz));
    }

    public <T> Optional<List<T>> getList(String key, TypeReference<List<T>> typeReference) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(result, typeReference));
    }

    public void delete(String key) {
        redisTemplate.unlink(key);
    }

    public void deleteAll(List<String> keys) {
        keys.forEach(redisTemplate::unlink);
    }
}
