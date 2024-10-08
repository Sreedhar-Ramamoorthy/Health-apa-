package ke.co.apollo.health.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.beans.BeanMap;

public class MappingUtils {

    private MappingUtils() {
    }

    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    @SuppressWarnings("squid:S2864")
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
        }
        return map;
    }

    @SuppressWarnings("squid:S2864")
    public static <T> Map<String, Object> beanToMapUppercase(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {

              map.put(String.valueOf(key)
                            .substring(0, 1)
                            .toUpperCase() + String.valueOf(key)
                                                   .substring(1), beanMap.get(key));
            }
        }
        return map;
    }
}
