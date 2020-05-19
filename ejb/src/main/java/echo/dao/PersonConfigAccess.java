package echo.dao;

import echo.entities.PersonConfig;
import echo.models.PersonConfigIdentifier;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
@LocalBean
public class PersonConfigAccess extends SimpleEntityAccess<PersonConfig> {
    public PersonConfigAccess() {
        super(PersonConfig.class);
    }

    public PersonConfig findOne(Long personId, PersonConfigIdentifier identifier) {
        String qlString = "select pc from PersonConfig pc where pc.person.id = :personId and pc.identifier = :identifier";
        List<PersonConfig> resultList = em.createQuery(qlString, PersonConfig.class)
                .setParameter("personId", personId)
                .setParameter("identifier", identifier)
                .getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
