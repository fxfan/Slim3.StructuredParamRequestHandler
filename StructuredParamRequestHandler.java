package xxx;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slim3.controller.RequestHandler;
import org.slim3.util.BeanDesc;
import org.slim3.util.BeanMap;
import org.slim3.util.BeanUtil;
import org.slim3.util.PropertyDesc;
import org.slim3.util.RequestMap;
import org.slim3.util.ThrowableUtil;

public class StructuredParamRequestHandler extends RequestHandler {

    private Map<String, Class<?>> beanClassMap = new HashMap<String, Class<?>>();

    public StructuredParamRequestHandler(HttpServletRequest request) {
        super(request);
    }

    public void addBeanClass(String name, Class<?> beanClass) {
        beanClassMap.put(name, beanClass);
    }

    private Object newBeanInstance(String name) {
        Class<?> beanClass = beanClassMap.get(name);
        if (beanClass == null) {
            return new BeanMap();
        }
        try {
            return beanClass.newInstance();
        } catch (Exception e) {
            throw ThrowableUtil.wrap(e);
        }
    }

    private static Pattern ARRAY_PATTERN = Pattern.compile("^([^\\[]+)\\[(\\d+)\\]$");

    /**
     * @throws ClassCastException
     *             同じ名前で違う型のパラメータが存在する場合
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handle() throws ClassCastException {

        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {

            String paramName = e.nextElement();
            String name = paramName;
            String beanName = null;
            int index = 0;
            Object bean = new RequestMap(request);

            if (name.endsWith(".")) {
                name = name.substring(0, name.length() - 1);
            }

            while ((index = name.indexOf('.')) >= 0) {
                String prefix = name.substring(0, index);
                name = name.substring(index + 1);
                Matcher m = ARRAY_PATTERN.matcher(prefix);
                if (m.find()) {
                    String arrayName = m.group(1);
                    int arrayIndex = Integer.parseInt(m.group(2));
                    beanName = beanName == null ? arrayName : beanName + "." + arrayName;
                    SortedMap<Integer, Object> array = PropUtil.get(bean, arrayName);
                    if (array == null) {
                        array = new TreeMap<Integer, Object>();
                        PropUtil.set(bean, arrayName, array);
                    }
                    bean = array.get(arrayIndex);
                    if (bean == null) {
                        bean = newBeanInstance(beanName);
                        array.put(arrayIndex, bean);
                    }
                } else {
                    beanName = beanName == null ? prefix : beanName + "." + prefix;
                    bean = PropUtil.get(bean, prefix);
                }
            }

            Matcher m = ARRAY_PATTERN.matcher(name);
            if (m.find()) {
                String arrayName = m.group(1);
                int arrayIndex = Integer.parseInt(m.group(2));
                SortedMap<Integer, Object> array = PropUtil.get(bean, arrayName);
                array.put(arrayIndex, getParamValue(arrayName, paramName));
            } else {
                if (PropUtil.get(bean, name) != null) {
                    continue;
                }
                PropUtil.set(bean, name, getParamValue(name, paramName));
            }
        }
    }

    private Object getParamValue(String propName, String paramName) {
        if (propName.endsWith(ARRAY_SUFFIX)) {
            return normalizeValues(request.getParameterValues(paramName));
        } else {
            return normalizeValue(request.getParameter(paramName));
        }
    }

    static class PropUtil {

        @SuppressWarnings("unchecked")
        static <T> T get(Object bean, String propertyName) {
            if (bean instanceof Map) {
                return ((Map<String, T>) bean).get(propertyName);
            }
            BeanDesc desc = BeanUtil.getBeanDesc(bean.getClass());
            PropertyDesc prop = desc.getPropertyDesc(propertyName);
            if (prop == null) {
                return null;
            }
            if (!prop.isReadable()) {
                throw new IllegalArgumentException("property '"
                    + propertyName
                    + "' of the bean is not found or not allowed to read.");
            }
            return (T) prop.getValue(bean);
        }

        @SuppressWarnings("unchecked")
        static void set(Object bean, String propertyName, Object value) {
            if (bean instanceof Map) {
                ((Map<String, Object>) bean).put(propertyName, value);
                return;
            }
            BeanDesc desc = BeanUtil.getBeanDesc(bean.getClass());
            PropertyDesc prop = desc.getPropertyDesc(propertyName);
            if (prop == null || !prop.isWritable()) {
                throw new IllegalArgumentException("property '"
                    + propertyName
                    + "' of the bean is not found or not allowed to write.");
            }
            prop.setValue(bean, value);
        }
    }

}
