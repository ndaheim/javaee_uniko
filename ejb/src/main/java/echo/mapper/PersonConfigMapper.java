package echo.mapper;

import echo.dto.PersonConfigDTO;
import echo.entities.Person;
import echo.entities.PersonConfig;

import java.util.List;
import java.util.stream.Collectors;

public class PersonConfigMapper extends SimpleMapper<PersonConfig, PersonConfigDTO> {
    @Override
    public PersonConfigDTO toDTO(PersonConfig entity, Class<PersonConfigDTO> clazz) {
        PersonConfigDTO dto = super.toDTO(entity, clazz);
        dto.setPersonId(entity.getPerson().getId());
        return dto;
    }

    @Override
    public PersonConfig toEntity(PersonConfigDTO dto, Class<PersonConfig> clazz) {
        PersonConfig personConfig = super.toEntity(dto, clazz);
        Person person = new Person();
        person.setId(dto.getPersonId());
        personConfig.setPerson(person);
        return personConfig;
    }

    @Override
    public List<PersonConfig> toEntity(List<PersonConfigDTO> dtoList, Class<PersonConfig> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<PersonConfigDTO> toDTO(List<PersonConfig> entityList, Class<PersonConfigDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }
}