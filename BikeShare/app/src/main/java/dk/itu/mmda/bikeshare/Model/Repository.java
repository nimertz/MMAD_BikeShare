package dk.itu.mmda.bikeshare.Model;

import java.util.List;

import dk.itu.mmda.bikeshare.Model.Specifications.RealmSpecification;

interface Repository<T> {
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    void remove(RealmSpecification specification);

    List<T> query(RealmSpecification specification);
}
