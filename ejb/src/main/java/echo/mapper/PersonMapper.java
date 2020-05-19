package echo.mapper;

import echo.dto.PersonDTO;
import echo.dto.RoleDTO;
import echo.entities.Person;
import echo.entities.Role;

import java.util.List;
import java.util.stream.Collectors;

public class PersonMapper extends SimpleMapper<Person, PersonDTO> {
    private SimpleMapper<Role, RoleDTO> roleMapper = new SimpleMapper<>();

    @Override
    public Person toEntity(PersonDTO dto, Class<Person> clazz) {
        Person person = super.toEntity(dto, clazz);
        person.setRoles(roleMapper.toEntity(dto.getRoles(), Role.class));
        return person;
    }

    @Override
    public PersonDTO toDTO(Person entity, Class<PersonDTO> clazz) {
        PersonDTO person = super.toDTO(entity, clazz);
        person.setRoles(roleMapper.toDTO(entity.getRoles(), RoleDTO.class));
        return person;
    }

    @Override
    public List<Person> toEntity(List<PersonDTO> dtoList, Class<Person> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<PersonDTO> toDTO(List<Person> entityList, Class<PersonDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }
}