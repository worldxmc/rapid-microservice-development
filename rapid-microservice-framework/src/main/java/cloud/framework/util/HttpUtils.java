package cloud.framework.util;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Http工具类
 *
 * @author xmc
 */
public class HttpUtils {

    /**
     * GET，无参，无自定义请求头
     */
    public static String get(String url) {
        return exchange(url, HttpMethod.GET, null, null, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * GET，有参，无自定义请求头
     */
    public static String get(String url, MultiValueMap<String, String> formParams) {
        return exchange(url, HttpMethod.GET, formParams, null, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * GET，有参，有自定义请求头
     */
    public static String get(String url, MultiValueMap<String, String> formParams, HashMap<String, String> headersMap) {
        return exchange(url, HttpMethod.GET, formParams, headersMap, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * POST，FormData
     */
    public static String postByForm(String url, MultiValueMap<String, String> formParams, HashMap<String, String> headersMap) {
        return exchange(url, HttpMethod.POST, formParams, headersMap, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * POST，Json
     */
    public static String postByJson(String url, String jsonParams, HashMap<String, String> headersMap) {
        return exchange(url, HttpMethod.POST, jsonParams, headersMap, MediaType.APPLICATION_JSON);
    }

    /**
     * 发送请求
     */
    private static <T> String exchange(String url, HttpMethod method, T params, HashMap<String, String> headersMap, MediaType mediaType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null && headersMap.size() > 0) {
            Set<Entry<String, String>> entries = headersMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        if (mediaType != null) {
            headers.setContentType(mediaType);
        }
        HttpEntity<T> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseResult = restTemplate.exchange(url, method, entity, String.class);
        return responseResult.getBody();
    }

}