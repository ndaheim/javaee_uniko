package echo.logic.impl;

import echo.dao.PersonAccess;
import echo.dto.PersonDTO;
import echo.logic.DashboardLogic;
import echo.mapper.PersonMapper;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
@LocalBean
public class DashboardLogicImpl implements DashboardLogic {
    private final PersonMapper personMapper = new PersonMapper();
    @EJB
    private PersonAccess personAccess;
    @Inject
    private Logger log;

    @Override
    public long getPersonCount() {
        return personAccess.count();
    }

    @Override
    public List<PersonDTO> getAllPersons() {
        return personMapper.toDTO(personAccess.findAll(), PersonDTO.class);
    }

}