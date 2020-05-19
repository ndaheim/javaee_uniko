package echo.dao;

import echo.entities.Contract;
import echo.entities.Person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@LocalBean
public class ContractAccess extends SimpleEntityAccess<Contract> {

    public ContractAccess() {
        super(Contract.class);
    }

    public List<Contract> getContractsByEmployee(Person person) {
        TypedQuery<Contract> query = em
                .createQuery("SELECT c FROM Contract c where c.employee = :employee", Contract.class)
                .setParameter("employee", person);
        return query.getResultList();
    }

    public List<Contract> getContractsBySecretary(Person person) {
        TypedQuery<Contract> query = em
                .createQuery("SELECT c FROM Contract c WHERE :secretary MEMBER OF c.secretaries", Contract.class)
                .setParameter("secretary", person);
        return query.getResultList();
    }

    public List<Contract> getContractsByDelegate(Person person) {
        TypedQuery<Contract> query = em
                .createQuery("SELECT c FROM Contract c WHERE :delegate MEMBER OF c.delegates", Contract.class)
                .setParameter("delegate", person);
        return query.getResultList();
    }

    public List<Contract> getContractsBySupervisor(Person person) {
        TypedQuery<Contract> query = em
                .createQuery("SELECT c FROM Contract c WHERE c.supervisor = :supervisor", Contract.class)
                .setParameter("supervisor", person);
        return query.getResultList();
    }

    public List<Contract> getContractsRelatedToPerson(Person person) {
        TypedQuery<Contract> query = em
                .createQuery("SELECT DISTINCT c FROM Contract c WHERE " +
                                "c.employee = :person OR " +
                                "c.supervisor = :person OR " +
                                ":person MEMBER OF c.secretaries OR " +
                                ":person MEMBER OF c.delegates",
                        Contract.class)
                .setParameter("person", person);
        return query.getResultList();
    }

}
