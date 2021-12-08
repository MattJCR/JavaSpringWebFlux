package com.springboot.webflux.app.services;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Flux<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Flux<Producto> findAllWithNameUppercase() {
        return productoRepository.findAll()
                .map(producto -> {
                    if(producto.getNombre() != null)
                        producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                });
    }

    @Override
    public Flux<Producto> findAllWithNameUppercaseRepeat(Long times) {
        return this.findAllWithNameUppercase().repeat(times);
    }

    @Override
    public Mono<Producto> findById(String id) {
        return productoRepository.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return productoRepository.deleteById(producto.getId());
    }
}
