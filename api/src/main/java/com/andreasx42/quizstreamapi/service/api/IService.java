package com.andreasx42.quizstreamapi.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T, S> {
    T getById(Long id);

    Page<S> getAll(Pageable pageable);

    void delete(Long id);

    S update(Long id, S dto);

}
