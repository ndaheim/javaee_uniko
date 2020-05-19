package echo.logic.impl;

import echo.dao.PersonAccess;
import echo.dao.RoleAccess;
import echo.dto.PersonDTO;
import echo.dto.RoleDTO;
import echo.entities.Person;
import echo.entities.Role;
import echo.logic.PersonConfigLogic;
import echo.logic.PersonManagementLogic;
import echo.mapper.PersonMapper;
import echo.mapper.SimpleMapper;
import echo.models.PersonConfigIdentifier;
import echo.util.StringUtils;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Stateless
@LocalBean
public class PersonManagementLogicImpl implements PersonManagementLogic {
    @EJB
    private PersonAccess personAccess;
    @EJB
    private RoleAccess roleAccess;
    @EJB
    private PersonConfigLogic personConfigLogic;

    @Inject
    private Logger log;

    private PersonMapper personMapper = new PersonMapper();
    private SimpleMapper<Role, RoleDTO> roleMapper = new SimpleMapper<>();

    @Override
    public List<PersonDTO> getAllPersons() {
        return personMapper.toDTO(personAccess.findAll(), PersonDTO.class);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleMapper.toDTO(roleAccess.findAll(), RoleDTO.class);
    }

    @Override
    public List<String> getUniRoles(long personId) {
        List<String> uniRoles = new ArrayList<>();

        String rolesString = personConfigLogic.getConfig(personId, PersonConfigIdentifier.UNIVERSITY_ROLES);
        if (StringUtils.isNotEmpty(rolesString)) {
            uniRoles = Arrays.asList(rolesString.split(","));
        }

        return uniRoles;
    }

    @Override
    public Optional<PersonDTO> getOneByEmail(String email) {
        Optional<Person> maybePerson = personAccess.findOneByEmail(email);
        return maybePerson.map(person -> personMapper.toDTO(person, PersonDTO.class));
    }

    @Override
    public long countOfAllPersons() {
        return personAccess.count();
    }

    @Override
    public PersonDTO save(PersonDTO personDTO) {
        Person toSave = personAccess.findOne(personDTO.getId());

        if (toSave == null) {
            toSave = personMapper.toEntity(personDTO, Person.class);
        }

        toSave.setFirstName(personDTO.getFirstName());
        toSave.setLastName(personDTO.getLastName());
        toSave.setDateOfBirth(personDTO.getDateOfBirth());
        toSave.setEmailAddress(personDTO.getEmailAddress());
        toSave.setRoles(roleMapper.toEntity(personDTO.getRoles(), Role.class));

        Person dbPerson = personAccess.save(toSave);
        return personMapper.toDTO(dbPerson, PersonDTO.class);
    }
}
