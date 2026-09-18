package repository;

import java.util.List;

/**
 * Generic repository interface defining standard CRUD operations.
 * Demonstrates: interfaces, generics, abstraction.
 *
 * @param <T> The entity type stored by this repository.
 */
public interface IRepository<T> {

    /**
     * Persist a new entity.
     */
    void save(T entity);

    /**
     * Retrieve all stored entities.
     * Returns an empty list if no entities exist.
     */
    List<T> getAll();

    /**
     * Find an entity by its unique string identifier.
     * Returns null if not found.
     */
    T findById(String id);

    /**
     * Delete the entity with the given ID.
     * @return true if an entity was deleted; false if not found.
     */
    boolean delete(String id);

    /**
     * Remove all stored entities.
     */
    void clear();
}
