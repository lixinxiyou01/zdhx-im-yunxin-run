package zhwx.common.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtil {

	@SuppressWarnings("unchecked")
	private static void setFieldValue(Field field, Object result, JSONObject jsonObj) throws Exception {
		if (jsonObj.isNull(field.getName())) {
			return;
		}
		if (Modifier.isFinal(field.getModifiers())) {
			return;
		}
		Object value = jsonObj.get(field.getName());
		setBaseTypeValue(field, result, value);
		JSONObject customTypeObj = jsonObj.optJSONObject(field.getName());
		if (customTypeObj != null) {
			setCustomType(field, result, customTypeObj);
		}
		JSONArray jsonArr = jsonObj.optJSONArray(field.getName());
		if (jsonArr != null) {
			Type genericType = field.getGenericType();
			if (genericType == null) {
				return;
			}
			Collection<Object> collectionResult = null;
			if (field.get(result) != null) {
				collectionResult = (Collection<Object>) field.get(result);
			}
			if (collectionResult == null) {
				if (field.getType().isAssignableFrom(List.class)) {
					collectionResult = new ArrayList<Object>();
				}
				if (field.getType().isAssignableFrom(Set.class)) {
					collectionResult = new HashSet<Object>();
				}
			}
			if (genericType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericType;
				@SuppressWarnings("rawtypes")
				Class genericClazz = (Class) pt.getActualTypeArguments()[0];
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject obj = jsonArr.getJSONObject(i);
					Object eleObj = genericClazz.newInstance();
					List<Field> fields = getAllField(genericClazz);
					for (Field f : fields) {
						setFieldValue(f, eleObj, obj);
					}
					collectionResult.add(eleObj);
				}
				field.set(result, collectionResult);
			}
		}
	}

	private static void setCustomType(Field field, Object result, JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return;
		}
		@SuppressWarnings("rawtypes")
		Class class1 = field.getType();
		List<Field> fields = getAllField(class1);
		Object customTypeResult = class1.newInstance();
		for (Field f : fields) {
			setFieldValue(f, customTypeResult, jsonObj);
		}
		field.set(result, customTypeResult);
	}

	private static void setBaseTypeValue(Field f, Object result, Object value) throws Exception {
		String typeString = f.getType().toString();
		f.setAccessible(true);
		if (typeString.endsWith("Boolean") || typeString.endsWith("boolean")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Byte") || typeString.endsWith("byte")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Character") || typeString.endsWith("char")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Double") || typeString.endsWith("double")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Float") || typeString.endsWith("float")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Integer") || typeString.endsWith("int")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Long") || typeString.endsWith("long")) {
			f.set(result, value);
		}
		if (typeString.endsWith("Short") || typeString.endsWith("short")) {
			f.set(result, value);
		}
		if (typeString.endsWith("String")) {
			f.set(result, value.toString());
		}
	}

	@SuppressWarnings("rawtypes")
	private static List<Field> getAllField(Class class1) {
		List<Field> fields = new ArrayList<Field>();
		List<String> names = new ArrayList<String>();
		Field[] fieldArr = class1.getDeclaredFields();
		for (Field f : fieldArr) {
			if (!names.contains(f.getName())) {
				fields.add(f);
				names.add(f.getName());
			}
		}
		@SuppressWarnings("unchecked")
		Class<? extends Class> superClass = class1.getSuperclass();
		if (superClass != null) {
			List<Field> superFields = getAllField(superClass);
			for (Field f : superFields) {
				if (!names.contains(f.getName())) {
					fields.add(f);
					names.add(f.getName());
				}
			}
		}
		return fields;
	}

	public static <T> List<T> json2List(String jsonString, Class<T> pojo) {
		try {
			List<T> result = new ArrayList<T>();
			JSONArray jsonArr = new JSONArray(jsonString);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject obj = jsonArr.getJSONObject(i);
				T t = pojo.newInstance();
				List<Field> fields = getAllField(pojo);
				for (Field f : fields) {
					setFieldValue(f, t, obj);
				}
				result.add(t);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static <T> T json2JavaPojo(String jsonString, Class<T> pojo) {
		try {
			List<Field> fields = getAllField(pojo);
			T result = pojo.newInstance();
			JSONObject jsonObj = new JSONObject(jsonString);
			for (Field f : fields) {
				setFieldValue(f, result, jsonObj);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * 把json串转成Map
	 * 
	 * @param jsonString
	 * @return key和value对应json中的key和value
	 */
	public static Map<String, String> json2Map(String jsonString) {
		Map<String, String> valueMap = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			valueMap = new HashMap<String, String>();

			while (keyIter.hasNext()) {
				String key = (String) keyIter.next();
				String value = jsonObject.get(key).toString();
				valueMap.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return valueMap;
	}
}
