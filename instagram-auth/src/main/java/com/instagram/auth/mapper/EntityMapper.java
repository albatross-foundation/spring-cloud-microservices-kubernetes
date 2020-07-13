package com.instagram.auth.mapper;

import java.util.List;

public interface EntityMapper<E, D> {
    D toDomain(E entity);

    E toEntity(D domain);

    List<D> toDomains(List<E> entities);

    List<E> toEntities(List<D> domains);
}
