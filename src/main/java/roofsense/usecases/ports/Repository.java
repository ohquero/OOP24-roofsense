package roofsense.usecases.ports;

import java.util.List;
import java.util.Optional;

public interface Repository<E> {

    Optional<E> get(String repositoryId);

    List<E> getAll();

    void add(E entity);

    E update(E entity);

    void remove(E entity);

}
