package echo.dao;

import echo.entities.Person;
import echo.models.RoleName;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
@LocalBean
public class PersonAccess extends SimpleEntityAccess<Person> {

    public PersonAccess() {
        super(Person.class);
    }

    public Optional<Person> findOneByEmail(String email) {
        TypedQuery<Person> query = em
                .createQuery("select p from Person p where p.emailAddress = :email", Person.class)
                .setParameter("email", email);
        return getFirstResult(query);
    }

    /**
     * Returns the Person for the given email
     *
     * @param email
     * @return the {@link Person} if no person was found null will be returned.
     */
    public Person findFirstByEmail(String email) {
        TypedQuery<Person> query = em
                .createQuery("select p from Person p where p.emailAddress = :email", Person.class)
                .setParameter("email", email);
        return orNull(query);
    }

    public List<Person> findAllByRole(RoleName roleName) {
        TypedQuery<Person> query = em
                .createQuery("select p from Person p join p.roles r where r.name = :roleName", Person.class)
                .setParameter("roleName", roleName);
        return query.getResultList();
    }

    public List<Person> findAllWithReminders() {
        TypedQuery<Person> query = em
                .createQuery("select p from Person p where p.reminders IS not empty ", Person.class);
        return query.getResultList();
    }

    public boolean existsByEmail(String emailAddress) {
        TypedQuery<Long> query = em
                .createQuery("select count(p.emailAddress) from Person p where p.emailAddress = :email", Long.class)
                .setParameter("email", emailAddress);
        return query.getResultList().get(0) > 0;
    }
}
