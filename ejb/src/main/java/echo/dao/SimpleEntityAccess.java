/*
 * JavaEE 2018 Demo Application
 */
package echo.dao;

import echo.entities.SimpleEntity;

import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class SimpleEntityAccess<T extends SimpleEntity> extends AbstractAccess {
    private final String entityName;
    private Class<T> entityClass;

    public SimpleEntityAccess(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.entityName = entityClass.getSimpleName();
    }

    /**
     * Find by primary key.
     * Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence context,
     * it is returned from there.
     *
     * @param id primary key
     * @return the found entity instance or null if the entity does
     * not exist
     * @throws IllegalArgumentException if the first argument does
     *                                  not denote an entity type or the second argument is
     *                                  is not a valid type for that entity's primary key or
     *                                  is null
     */
    public T findOne(long id) {
        return em.find(entityClass, id);
    }

    /**
     * Checks whether the entity exists for the specified id.
     *
     * @param id the id of the entity that should be checked.
     * @return <code>true</code> if the entity exists for the given id; <code>false</code> otherwise.
     */
    public boolean exists(long id) {
        return findOne(id) != null;
    }

    /**
     * Merge the state of the given entity into the
     * current persistence context.
     *
     * @param entity entity instance
     * @return the managed instance that the state was merged to or null if null was entered
     * @throws IllegalArgumentException     if instance is not an
     *                                      entity or is a removed entity
     * @throws TransactionRequiredException if there is no transaction when
     *                                      invoked on a container-managed entity manager of that is of type
     *                                      <code>PersistenceContextType.TRANSACTION</code>
     */
    public T save(T entity) {
        if (entity != null) {
            return em.merge(entity);
        }

        return null;
    }

    /**
     * Deletes the entity if it exists.
     *
     * @param id the id of the entity instance
     * @throws TransactionRequiredException if invoked on a
     *                                      container-managed entity manager of type
     *                                      <code>PersistenceContextType.TRANSACTION</code> and there is
     *                                      no transaction
     */
    public void delete(long id) {
        T entity = findOne(id);

        if (entity != null) {
            em.remove(entity);
        }
    }

    /**
     * Deletes a list of echo.entities.
     *
     * @param entities the list of the entity instances
     * @throws TransactionRequiredException if invoked on a
     *                                      container-managed entity manager of type
     *                                      <code>PersistenceContextType.TRANSACTION</code> and there is
     *                                      no transaction
     */
    public void delete(Iterable<T> entities) {
        entities.forEach(this::delete);
    }

    /**
     * Deletes the entity if it exists.
     *
     * @param entity the managed entity instance
     * @throws TransactionRequiredException if invoked on a
     *                                      container-managed entity manager of type
     *                                      <code>PersistenceContextType.TRANSACTION</code> and there is
     *                                      no transaction
     */
    public void delete(T entity) {
        if (entity != null) {
            if (!em.contains(entity)) {
                entity = em.merge(entity);
            }
            em.remove(entity);
        }
    }

    /**
     * Counts all existing echo.entities.
     *
     * @return the number of all existing echo.entities in the persistence context.
     */
    public long count() {
        return em.createQuery("select count(e) from " + entityName + " e", Long.class).getSingleResult();
    }

    /**
     * Finds all entries the given entity.
     *
     * @return A list of all existing echo.entities.
     */
    public List<T> findAll() {
        TypedQuery<T> query = em.createQuery("select e from " + entityName + " e", entityClass);
        return query.getResultList();
    }

    Optional<T> getFirstResult(TypedQuery<T> typedQuery) {
        List<T> resultList = typedQuery.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    T orNull(TypedQuery<T> typedQuery) {
        List<T> resultList = typedQuery.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
