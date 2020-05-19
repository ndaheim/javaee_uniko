/*
 * JavaEE 2018 Demo Application
 */
package echo.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

abstract class AbstractAccess {
    @PersistenceContext(unitName = "default")
    EntityManager em;
}
