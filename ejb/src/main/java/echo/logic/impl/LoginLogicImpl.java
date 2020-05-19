package echo.logic.impl;

import echo.dao.PersonAccess;
import echo.dao.RoleAccess;
import echo.dto.PersonDTO;
import echo.entities.Person;
import echo.logic.LoginLogic;
import echo.logic.PersonConfigLogic;
import echo.mapper.PersonMapper;
import echo.models.PersonConfigIdentifier;
import echo.models.RoleName;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

@Stateless
@LocalBean
public class LoginLogicImpl implements LoginLogic {
    private static final boolean LDAP_OFFLINE_MODE = false;
    private static final String LDAP_HOST = "foo";
    private static final String LDAP_PORT = "bar";
    private static final String UNI_MAIL_POSTFIX = "@uni-koblenz.de";

    @EJB
    private PersonAccess personAccess;
    @EJB
    private RoleAccess roleAccess;
    @EJB
    private PersonConfigLogic personConfigLogic;

    @Inject
    private Logger log;

    private PersonMapper personMapper = new PersonMapper();

    public Optional<PersonDTO> readPersonFromLdap(String username) {
        return readPersonFromLdap(username, null);
    }

    public Optional<PersonDTO> readPersonFromLdap(String username, String password) {
        try {
            username = username.trim();

            if (username.endsWith(UNI_MAIL_POSTFIX)) {
                username = username.replace(UNI_MAIL_POSTFIX, "");
            }

            PersonDTO ldapPerson = new PersonDTO();
            LdapResult ldapResult = new LdapResult();

            if (LDAP_OFFLINE_MODE) {
                ldapPerson.setEmailAddress("maxmuster" + UNI_MAIL_POSTFIX);
                ldapPerson.setFirstName("Max");
                ldapPerson.setLastName("Muster");
            } else {
                // throws exception if login was not successful in ldap
                InitialDirContext context;
                if (password == null) {
                    context = getLdapContext();
                } else {
                    context = getLdapContext(username, password);
                }

                ldapResult = readUserFromLdap(context, username);
                ldapPerson = ldapResult.person;
            }

            Person dbPerson = persistPerson(ldapPerson, ldapResult);

            return Optional.ofNullable(personMapper.toDTO(dbPerson, PersonDTO.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Person persistPerson(PersonDTO ldapPerson, LdapResult ldapResult) {
        Optional<Person> maybeDbPerson = personAccess.findOneByEmail(ldapPerson.getEmailAddress());

        Person dbPerson;
        if (maybeDbPerson.isPresent()) {
            dbPerson = maybeDbPerson.get();

            // Update the existing person with new data from ldap
            dbPerson.setFirstName(ldapPerson.getFirstName());
            dbPerson.setLastName(ldapPerson.getLastName());
            dbPerson.setEmailAddress(ldapPerson.getEmailAddress());

            dbPerson = personAccess.save(dbPerson);
        } else {
            Person person = personMapper.toEntity(ldapPerson, Person.class);
            person.getRoles().add(roleAccess.getRole(RoleName.GUEST));
            dbPerson = personAccess.save(person);
        }

        saveLdapGroups(dbPerson, ldapResult.groups);
        return dbPerson;
    }

    private void saveLdapGroups(Person person, List<String> groups) {
        personConfigLogic.setConfig(
                person.getId(),
                PersonConfigIdentifier.UNIVERSITY_ROLES,
                String.join(",", groups));
    }

    private InitialDirContext getLdapContext() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + LDAP_HOST + ":" + LDAP_PORT);
        return new InitialDirContext(env);
    }

    private InitialDirContext getLdapContext(String username, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + LDAP_HOST + ":" + LDAP_PORT);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=" + username + ",ou=people,dc=uni-koblenz,dc=de");
        env.put(Context.SECURITY_CREDENTIALS, password);
        return new InitialDirContext(env);
    }

    private LdapResult readUserFromLdap(InitialDirContext context, String uid) throws Exception {
        NamingEnumeration<SearchResult> searchResult =
                context.search("ou=people,dc=uni-koblenz,dc=de", "uid=" + uid, createConstraints());

        if (searchResult.hasMore()) {
            Attributes attributes = searchResult.next().getAttributes();

            PersonDTO personDTO = new PersonDTO();
            personDTO.setEmailAddress(readLdapAttribute(attributes, LdapAttribute.EMAIL));
            personDTO.setFirstName(readLdapAttribute(attributes, LdapAttribute.FIRST_NAME));
            personDTO.setLastName(readLdapAttribute(attributes, LdapAttribute.LAST_NAME));
            List<String> groups = readLdapAttributes(attributes, LdapAttribute.GROUPS);

            return new LdapResult(personDTO, groups);
        } else {
            throw new IllegalArgumentException(String.format("found no ldap user for uid: %s", uid));
        }
    }

    private String readLdapAttribute(Attributes attrs, LdapAttribute ldapAttribute) {
        String attributeValue = null;
        try {
            Attribute attribute = attrs.get(ldapAttribute.getAttributeId());
            if (attribute != null && attribute.size() >= 1 && attribute.get() != null) {
                attributeValue = attribute.get().toString();
            }
        } catch (NamingException e) {
            log.warn(e.getMessage(), e);
        }
        return attributeValue;
    }

    private List<String> readLdapAttributes(Attributes attrs, LdapAttribute ldapAttribute) {
        List<String> attributeValue = new ArrayList<>();
        try {
            Attribute attribute = attrs.get(ldapAttribute.getAttributeId());
            if (attribute != null && attribute.size() >= 1) {
                NamingEnumeration<?> all = attribute.getAll();
                while (all.hasMore()) {
                    Object next = all.next();
                    if (next != null) {
                        attributeValue.add(next.toString());
                    }
                }
            }
        } catch (NamingException e) {
            log.warn(e.getMessage(), e);
        }
        return attributeValue;
    }

    private SearchControls createConstraints() {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(LdapAttribute.getAttributeIds());
        return constraints;
    }

    private enum LdapAttribute {
        USERNAME("uid"),
        FIRST_NAME("givenName"),
        LAST_NAME("sn"),
        FULL_NAME("cn"),
        EMAIL("mail"),
        GROUPS("eduPersonAffiliation");

        private String attributeId;

        LdapAttribute(String attributeId) {
            this.attributeId = attributeId;
        }

        public static String[] getAttributeIds() {
            return Arrays.stream(values()).map(LdapAttribute::getAttributeId).toArray(String[]::new);
        }

        public String getAttributeId() {
            return attributeId;
        }
    }

    private class LdapResult {
        PersonDTO person;
        List<String> groups = new ArrayList<>();

        LdapResult() {
        }

        LdapResult(PersonDTO person, List<String> groups) {
            this.person = person;
            this.groups = groups;
        }
    }
}




