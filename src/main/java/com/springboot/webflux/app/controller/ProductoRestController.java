package com.springboot.webflux.app.controller;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.repository.ProductoRepository;
import com.springboot.webflux.app.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoRestController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @GetMapping()
    public Flux<Producto> getAllProductos(){

        Flux<Producto> productos = productoService.findAllWithNameUppercase();
        productos.subscribe();
        return productos;
    }
    @GetMapping("/{id}")
    public Mono<Producto> getProducto(@PathVariable String id){

        //Esta forma es simplemente para practicar los FLuxs y Monos
        Flux<Producto> productos = productoService.findAll();
        //Filtramos por ID y recogemos solamente el primer Objecto con Next
        Mono<Producto> producto = productos
                .filter(p-> p.getId().equals(id))
                .next();
        return producto;
        //La forma correcta sería la siguiente
        //Mono<Producto> producto = productoRepository.findById(id);
        //return producto;
    }
}
