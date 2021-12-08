package com.springboot.webflux.app.services;

import com.springboot.webflux.app.models.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {
    Flux<Producto> findAll();

    //Devuelve los nombres de los productos en mayusculas
    Flux<Producto> findAllWithNameUppercase();
    //Devuelve findAllWithNameUppercase() repitiendo los resultados n veces
    Flux<Producto> findAllWithNameUppercaseRepeat(Long times);
    Mono<Producto> findById(String id);
    Mono<Producto> save(Producto producto);
    Mono<Void> delete(Producto producto);
}
