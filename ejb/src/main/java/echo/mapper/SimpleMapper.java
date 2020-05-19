package echo.mapper;

import echo.dto.SimpleDTO;
import echo.entities.SimpleEntity;
import org.glassfish.api.logging.LogLevel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Maps every field by field name between two given classes. If a field is instance of {@link SimpleEntity} or
 * {@link SimpleDTO}, the field will not gets mapped.
 *
 * @param <E> The source class
 * @param <D> The target class
 */
public class SimpleMapper<E, D> {
    private static void mapFields(Object from, Object to) throws IllegalAccessException {
        List<Field> fromFields = getAllFields(new ArrayList<>(), from.getClass());
        List<Field> toFields = getAllFields(new ArrayList<>(), to.getClass());

        for (Field fromField : fromFields) {
            try {
                fromField.setAccessible(true);
                Object fromValue = fromField.get(from);
                Optional<Field> maybeToField = toFields.stream()
                        .filter(x -> x.getName().equals(fromField.getName()))
                        .findFirst();

                if (maybeToField.isPresent()
                        && !SimpleDTO.class.isAssignableFrom(maybeToField.get().getType())
                        && !SimpleEntity.class.isAssignableFrom(maybeToField.get().getType())
                ) {
                    Field field = maybeToField.get();
                    field.setAccessible(true);
                    field.set(to, fromValue);
                }
            } catch (Exception e) {
                Logger.getLogger(SimpleMapper.class.getCanonicalName()).log(LogLevel.SEVERE, e.getMessage(), e);
            }
        }
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public E toEntity(D dto, Class<E> clazz) {
        try {
            Constructor<E> constructor = clazz.getConstructor();
            E entity = constructor.newInstance();
            mapFields(dto, entity);
            return entity;
        } catch (Exception e) {
            Logger.getLogger(SimpleMapper.class.getCanonicalName()).log(LogLevel.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    public List<E> toEntity(List<D> dtoList, Class<E> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    public List<D> toDTO(List<E> entityList, Class<D> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }

    public D toDTO(E entity, Class<D> clazz) {
        try {
            Constructor<D> constructor = clazz.getConstructor();
            D dto = constructor.newInstance();
            mapFields(entity, dto);
            return dto;
        } catch (Exception e) {
            Logger.getLogger(SimpleMapper.class.getCanonicalName()).log(LogLevel.SEVERE, e.getMessage(), e);
        }

        return null;
    }
}