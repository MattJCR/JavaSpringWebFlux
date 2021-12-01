package com.springboot.webflux.app.controller;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Locale;

@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoRepository productoRepository;

    //Muestra un listado de produtos. Es igual que con MVC pero con objectos reactivos
    @GetMapping({"/listar", "/"})
    public String listar(Model model){
        log.info("GetMapping -> listar");
        Flux<Producto> productos = productoRepository.findAll()
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                });
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos");
        model.addAttribute("productos",productos);
        return "listar";
    }


    //Este metodo devuelve con un delay en tiempo real los elementos a mostrar en la vista
    //Para este caso usamos una de las formas con ReactiveDataDriverContextVariable
    @GetMapping({"/listar-datadriver"})
    public String listarDataDriver(Model model){
        log.info("GetMapping -> listar-datadriver");
        Flux<Producto> productos = productoRepository.findAll()
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                }).delayElements(Duration.ofSeconds(1));
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos DataDriver");
        //Con ReactiveDataDriverContextVariable podemos enviar elementos a la vista por lotes
        //En este ejemplo se puede observar como se van enviando en lotes de 1
        //Es util para enviar una gran cantidad de datos y evitar los tiempos de espera.
        ReactiveDataDriverContextVariable dataDriver =
                new ReactiveDataDriverContextVariable(productos,1);
        model.addAttribute("productos",dataDriver);
        return "listar";
    }

    //Este metodo devuelve con un delay en tiempo real los elementos a mostrar en la vista
    //Para este metodo vamos a usar un Chunk en modo Full
    @GetMapping({"/listar-chunkfull"})
    public String listarChunkFull(Model model){
        log.info("GetMapping -> listar-chunkfull");
        //Debemos de configurar el chunk size desde el aplication.properties
        //Por defecto este está en 0 pero para simular un numero de bytes
        //que se van enviando vamos a cambiarlo a 1024 0 512
        //Ejemplo: spring.thymeleaf.reactive.max-chunk-size=1024
        Flux<Producto> productos = productoRepository.findAll()
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                }).repeat(5000);
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos DataDriver");

        model.addAttribute("productos",productos);
        return "listar";
    }
}
