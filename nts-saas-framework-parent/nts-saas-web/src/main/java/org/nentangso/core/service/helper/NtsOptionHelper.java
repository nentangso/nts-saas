package org.nentangso.core.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.annotation.OptionProperties;
import org.nentangso.core.domain.NtsOptionEntity;
import org.nentangso.core.repository.NtsOptionRepository;
import org.nentangso.core.service.errors.NtsValidationException;
import org.nentangso.core.service.utils.NtsTextUtils;
import org.nentangso.core.service.utils.NtsValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConditionalOnProperty(
    prefix = "nts.helper.option",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsOptionHelper {
    private static final Logger log = LoggerFactory.getLogger(NtsOptionHelper.class);

    private final NtsOptionRepository optionRepository;

    public NtsOptionHelper(NtsOptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Optional<String> readRawString(String optionKey) {
        if (StringUtils.isBlank(optionKey)) {
            return Optional.empty();
        }
        return optionRepository.findOneByOptionKey(optionKey)
            .map(NtsOptionEntity::getOptionValue);
    }

    @Transactional
    public void writeRawString(String optionKey, String optionValue) {
        if (StringUtils.isBlank(optionKey)) {
            throw new NtsValidationException("option_key", "Option key is invalid");
        }
        NtsOptionEntity option = optionRepository.findOneByOptionKey(optionKey)
            .orElseGet(() -> new NtsOptionEntity(optionKey, optionValue));
        option.setOptionValue(optionValue);
        optionRepository.save(option);
    }

    public Optional<Boolean> readBoolean(String optionKey) {
        return readRawString(optionKey)
            .map(Boolean::parseBoolean);
    }

    @Transactional
    public void writeBoolean(String optionKey, Boolean value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<Long> readLong(String optionKey) {
        return readRawString(optionKey)
            .map(Long::parseLong);
    }

    @Transactional
    public void writeLong(String optionKey, Long value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<Integer> readInteger(String optionKey) {
        return readRawString(optionKey)
            .map(Integer::parseInt);
    }

    @Transactional
    public void writeInteger(String optionKey, Integer value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<Float> readFloat(String optionKey) {
        return readRawString(optionKey)
            .map(Float::parseFloat);
    }

    @Transactional
    public void writeFloat(String optionKey, Float value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<Double> readDouble(String optionKey) {
        return readRawString(optionKey)
            .map(Double::parseDouble);
    }

    @Transactional
    public void writeUUID(String optionKey, UUID value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<UUID> readUUID(String optionKey) {
        return readRawString(optionKey)
            .map(UUID::fromString);
    }

    @Transactional
    public void writeBigDecimal(String optionKey, BigDecimal value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public Optional<BigDecimal> readBigDecimal(String optionKey) {
        return readRawString(optionKey)
            .map(BigDecimal::new);
    }

    @Transactional
    public void writeDouble(String optionKey, Double value) {
        String rawString = value == null ? null : String.valueOf(value);
        writeRawString(optionKey, rawString);
    }

    public <T> Optional<T> read(Class<T> clazz) {
        if (clazz == null) {
            log.warn("Class cannot be null");
            return Optional.empty();
        }
        String prefix = getPrefix(clazz);
        T output;
        try {
            output = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("Class must has constructor without parameters");
            return Optional.empty();
        }
        Set<String> optionKeys = Stream.of(clazz.getDeclaredFields())
            .map(field -> generateOptionKey(field, prefix))
            .collect(Collectors.toSet());
        List<NtsOptionEntity> options = optionRepository.findByOptionKeyIn(optionKeys);
        if (options.isEmpty()) {
            NtsValidationUtils.validateObject(output);
            return Optional.of(output);
        }
        for (Field field : clazz.getDeclaredFields()) {
            try {
                setValues(output, field, options, prefix);
            } catch (IllegalAccessException e) {
                log.warn("Cannot set field " + field.getName());
                return Optional.empty();
            }
        }
        NtsValidationUtils.validateObject(output);
        return Optional.of(output);
    }

    private String getPrefix(Class<?> clazz) {
        OptionProperties optionProperties = clazz.getAnnotation(OptionProperties.class);
        if (optionProperties == null) {
            log.warn("Class must has annotation OptionProperties");
            return "";
        }
        String prefix = Optional.of(optionProperties.prefix())
            .filter(StringUtils::isNotBlank)
            .map(m -> StringUtils.trim(m) + ".")
            .orElse("");
        return prefix;
    }

    private <T> void setValues(T output, Field field, List<NtsOptionEntity> options, String prefix) throws IllegalAccessException {
        String optionKey = generateOptionKey(field, prefix);
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            String typeName = parameterizedType.getRawType().getTypeName();
            String childrenTypeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
            List<Object> values = options.stream()
                .filter(f -> StringUtils.equals(f.getOptionKey(), optionKey))
                .map(NtsOptionEntity::getOptionValue)
                .filter(Objects::nonNull)
                .map(rawString -> parseValue(field, rawString, childrenTypeName))
                .collect(Collectors.toList());
            Object fieldValues;
            if (StringUtils.equals(typeName, List.class.getTypeName())) {
                fieldValues = values;
            } else if (StringUtils.equals(typeName, Set.class.getTypeName())) {
                fieldValues = new HashSet<>(values);
            } else {
                throw new ClassCastException("Type of " + field.getName() + " is not supported");
            }
            field.setAccessible(true);
            field.set(output, fieldValues);
        } else {
            String rawString = options.stream()
                .filter(f -> StringUtils.equals(f.getOptionKey(), optionKey))
                .map(NtsOptionEntity::getOptionValue)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
            if (rawString == null) return;
            Object value = parseValue(field, rawString, field.getType().getTypeName());
            field.setAccessible(true);
            field.set(output, value);
        }
    }

    private Object parseValue(Field field, String rawString, String typeName) {
        Object value;
        if (StringUtils.equals(typeName, String.class.getTypeName())) {
            value = rawString;
        } else if (StringUtils.equalsAny(typeName, Integer.class.getTypeName(), "int")) {
            value = Integer.parseInt(rawString);
        } else if (StringUtils.equalsAny(typeName, Long.class.getTypeName(), "long")) {
            value = Long.parseLong(rawString);
        } else if (StringUtils.equalsAny(typeName, Float.class.getTypeName(), "float")) {
            value = Float.parseFloat(rawString);
        } else if (StringUtils.equalsAny(typeName, Double.class.getTypeName(), "double")) {
            value = Double.parseDouble(rawString);
        } else if (StringUtils.equals(typeName, BigDecimal.class.getTypeName())) {
            value = new BigDecimal(rawString);
        } else {
            throw new ClassCastException("Type of " + field.getName() + " is not supported");
        }
        return value;
    }

    private String generateOptionKey(Field field, String prefix) {
        String optionKey = StringUtils.join(prefix, NtsTextUtils.toSnakeCase(field.getName()));
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            if (isSupportedGenericType((ParameterizedType) genericType)) {
                return optionKey;
            }
        } else {
            if (isSupportedTypeName(field.getType().getTypeName())) {
                return optionKey;
            }
        }
        throw new ClassCastException("Type of " + field.getName() + " is not supported");
    }

    private boolean isSupportedGenericType(ParameterizedType parameterizedType) {
        String typeName = parameterizedType.getRawType().getTypeName();
        if (StringUtils.equalsAny(typeName, List.class.getTypeName(), Set.class.getTypeName())) {
            String childrenTypeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
            return isSupportedTypeName(childrenTypeName);
        }
        return false;
    }

    private boolean isSupportedTypeName(String typeName) {
        return StringUtils.equalsAny(
            typeName,
            String.class.getTypeName(),
            Integer.class.getTypeName(), "int",
            Long.class.getTypeName(), "long",
            Float.class.getTypeName(), "float",
            Double.class.getTypeName(), "double",
            BigDecimal.class.getTypeName()
        );
    }

    @Transactional
    public <T> void write(T configuration) {
        if (configuration == null) {
            log.warn("Configuration cannot be null");
            return;
        }
        NtsValidationUtils.validateObject(configuration);
        Class<?> clazz = configuration.getClass();
        String prefix = getPrefix(clazz);
        List<NtsOptionEntity> options = new ArrayList<>();
        Set<String> optionKeys = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            String optionKey = generateOptionKey(field, prefix);
            optionKeys.add(optionKey);
            try {
                field.setAccessible(true);
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Collection<?> values = (Collection<?>) field.get(configuration);
                    String childrenTypeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
                    values.forEach(value -> options.add(new NtsOptionEntity(optionKey, convertValue(value, childrenTypeName))));
                } else {
                    Object value = field.get(configuration);
                    String rawValue = convertValue(value, field.getType().getTypeName());
                    options.add(new NtsOptionEntity(optionKey, rawValue));
                }
            } catch (IllegalAccessException e) {
                throw new ClassCastException("Type of " + field.getName() + " is not supported");
            }
        }
        save(optionKeys, options);
    }

    private void save(Set<String> optionKeys, List<NtsOptionEntity> options) {
        List<NtsOptionEntity> existOptions = optionRepository.findByOptionKeyIn(optionKeys);
        List<NtsOptionEntity> addingOptions = new ArrayList<>();
        for (NtsOptionEntity option : options) {
            NtsOptionEntity existOption = existOptions.stream()
                .filter(f -> StringUtils.equals(f.getOptionKey(), option.getOptionKey()) && StringUtils.equals(f.getOptionValue(), option.getOptionValue()))
                .findFirst()
                .orElse(null);
            if (existOption != null) {
                existOptions.remove(existOption);
            } else {
                addingOptions.add(option);
            }
        }
        optionRepository.deleteAll(existOptions);
        optionRepository.saveAll(addingOptions);
        optionRepository.flush();
    }

    private String convertValue(Object value, String typeName) {
        String rawString;
        if (StringUtils.equals(typeName, String.class.getTypeName())) {
            rawString = value == null ? null : (String) value;
        } else if (StringUtils.equalsAny(typeName, Integer.class.getTypeName(), Long.class.getTypeName(), Float.class.getTypeName(), Double.class.getTypeName(), BigDecimal.class.getTypeName())) {
            rawString = value == null ? null : String.valueOf(value);
        } else if (StringUtils.equalsAny(typeName, "int", "long", "float", "double")) {
            rawString = value == null ? "0" : String.valueOf(value);
        } else {
            throw new ClassCastException("Type " + typeName + " is not supported");
        }
        return rawString;
    }
}
