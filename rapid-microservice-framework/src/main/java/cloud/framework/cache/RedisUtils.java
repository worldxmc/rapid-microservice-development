package cloud.framework.cache;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author xmc
 */
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * lua脚本执行成功返回值
     */
    private static final Long LUA_SCRIPT_SUCCESS = 1L;

    public RedisUtils() {
    }

    /**
     * 设置key和value的值
     */
    public boolean set(String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = this.redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return result;
    }

    /**
     * 设置key和value的值，同时设置过期时间
     */
    public boolean set(String key, Object value, Long expireTime) {
        boolean result = false;
        if (this.set(key, value)) {
            this.redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        }
        return result;
    }

    /**
     * 判断指定key是否存在
     */
    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key);
    }

    /**
     * 为指定key设置过期时间（秒）
     */
    public boolean expire(String key, long time) {
        if(time <= 0L){
           return false;
        }
        if(this.exists(key)){
           this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
           return true;
        }
        return false;
    }

    /**
     * 根据key获取value的值
     */
    public Object get(String key) {
        ValueOperations<Serializable, Object> operations = this.redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 获取所有符合正则匹配规则的key对应的value值
     */
    public <T> List<T> getAll(String pattern, Class<T> clazz) {
        List<T> result = Lists.newArrayList();
        Set<String> keys = this.redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            keys.stream().forEach( (k) -> {
                    if(clazz.isAssignableFrom(HashMap.class)){
                        result.add((T) this.hashGet(k));
                    }else {
                        result.add((T) this.get(k));
                    }
            });
        }
        return result;
    }

    /**
     * 根据key获取过期时间（秒）
     */
    public long getExpire(String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 删除指定key
     */
    public void delete(String key) {
        if (this.exists(key)) {
            this.redisTemplate.delete(key);
        }

    }

    /**
     * 批量删除key
     */
    public void delete(String... keys) {
        String[] keysArray = keys;
        for(int i = 0; i < keysArray.length; i++) {
            String key = keysArray[i];
            this.delete(key);
        }
    }

    /**
     * 根据正则匹配规则删除key
     */
    public void deletePattern(String pattern) {
        Set<Serializable> keys = this.redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            this.redisTemplate.delete(keys);
        }
    }

    /**
     * 重命名key
     */
    public void rename(String oldKey, String newKey) {
        if (this.exists(oldKey)) {
            this.redisTemplate.rename(oldKey, newKey);
        }
    }

    /**
     * 根据key获取hashValue
     */
    public Map<Object, Object> hashGet(String key) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        return hash.entries(key);
    }

    /**
     * 根据key获取hashValue中指定字段的值
     */
    public Object hashGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 根据key获取hashValue中所有字段
     */
    public Set<Object> hashKeys(String key) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        return hash.keys(key);
    }

    /**
     * 设置指定key下hashValue中指定字段的值
     */
    public void hashSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 设置指定key下hashValue中多个字段的值
     */
    public void hashSet(String key, Map<Object, Object> map) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        hash.putAll(key, map);
    }

    /**
     * 删除指定key下hashValue中指定字段
     */
    public void hashDelete(String masterKey, Object... hashKey) {
        HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
        hash.delete(masterKey, hashKey);
    }

    /**
     * 设置锁（成功返回true）
     */
    public boolean lock(String key, String value, long timeout) {
        return this.redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放锁（成功返回true）
     *
     * 基于以下lua脚本实现：
     * if redis.call("get",KEYS[1]) == ARGV[1] then
     *     return redis.call("del",KEYS[1])
     * else
     *     return 0
     * end
     */
    public boolean unlock(String key, String value) {
        List<String> keys = Collections.singletonList(key);
        Object result = this.redisTemplate.execute(new DefaultRedisScript("if redis.call('get',KEYS[1]) == ARGV[1]\nthen\n    return redis.call('del',KEYS[1])\nelse\n    return 0\nend", Object.class), keys, new Object[]{value});
        return LUA_SCRIPT_SUCCESS.equals(result);
    }

}