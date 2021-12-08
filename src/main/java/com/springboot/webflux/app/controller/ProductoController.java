package com.springboot.webflux.app.controller;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

//SessionAttributes permite guardar el estado de un atributo hasta que se completa
@SessionAttributes("producto")
@Controller
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    //Muestra un listado de produtos. Es igual que con MVC pero con objectos reactivos
    @GetMapping({"/listar", "/"})
    public Mono<String> listar(Model model){
        log.info("GetMapping -> listar");
        Flux<Producto> productos = productoService.findAllWithNameUppercase();
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos");
        model.addAttribute("productos",productos);
        return Mono.just("listar");
    }
    //Muestra la vista del form al que pasamos previamente una nueva instancia de producto
    @GetMapping("/form")
    public Mono<String> crearProducto(Model model){
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo","Formulario de Producto");
        return Mono.just("form");
    }

    //Muestra la vista del form pero pasando la isntancia de un producto existente para editarlo.
    //Por defecto si no encuentra el producto devuelve una instancia nueva de Producto.
    @GetMapping("/form/{id}")
    public Mono<String> editarProducto(Model model, @PathVariable String id){
        Mono<Producto> producto = productoService.findById(id).defaultIfEmpty(new Producto());
        model.addAttribute("producto", producto);
        model.addAttribute("titulo","Formulario Editar Producto");
        return Mono.just("form");
    }

    //Recogemos el producto enviado en el submit del form, lo guardamos y redireccionamos a
    //la vista de listar para visualizarlo
    //Si utilizamos SessionAttributes como anotacion en el controlador
    //Guardamos el estado del atributo (Con Id incluido si es para editar)
    //Debemos pasarlo en el metodo donde se va a poner como completado para borrarlo
    @PostMapping("/form")
    public Mono<String> guardarProducto(Producto producto, SessionStatus sessionStatus){
        //Se marca como completada la sesion temporal del producto
        sessionStatus.isComplete();
        log.info("PRODUCTO AÑADIDO: " + producto.toString());
        return productoService.save(producto).thenReturn("redirect:/listar");
    }

    //Este metodo devuelve con un delay en tiempo real los elementos a mostrar en la vista
    //Para este caso usamos una de las formas con ReactiveDataDriverContextVariable
    @GetMapping({"/listar-datadriver"})
    public String listarDataDriver(Model model){
        log.info("GetMapping -> listar-datadriver");
        Flux<Producto> productos = productoService.findAllWithNameUppercase().delayElements(Duration.ofSeconds(1));
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
    //Cuando está en modo chunk (con contrapresion) el tiempo de espera se reduce
    @GetMapping({"/listar-chunkfull"})
    public String listarChunkFull(Model model){
        log.info("GetMapping -> listar-chunkfull");
        //Debemos de configurar el chunk size desde el aplication.properties
        //Por defecto este está en 0 pero para simular un numero de bytes
        //que se van enviando vamos a cambiarlo a 1024 0 512
        //Ejemplo: spring.thymeleaf.reactive.max-chunk-size=1024
        Flux<Producto> productos = productoService.findAllWithNameUppercaseRepeat(5000L);
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos DataDriver");

        model.addAttribute("productos",productos);
        return "listar";
    }

    //Este metodo devuelve con un delay en tiempo real los elementos a mostrar en la vista
    //Para este metodo vamos a usar un Chunk configurado con view name
    @GetMapping({"/listar-chunkviewname"})
    public String listarChunkViewName(Model model){
        log.info("GetMapping -> listar-chunkviewname");
        //Debemos de configurar el chunk view-name desde el aplication.properties
        //Por defecto como se ve en el ejemplo anterior se aplica a todas las vistas
        //Pero podemos especificar que sea solamente a una o varias vistas concretas
        //Ejemplo: spring.thymeleaf.reactive.chunked-mode-view-names=listar-chunked
        //Incluidos directorios si tenemos la estructura de directorios
        //Ejemplo: spring.thymeleaf.reactive.chunked-mode-view-names=directorio/listar-chunked

        Flux<Producto> productos = productoService.findAllWithNameUppercaseRepeat(5000L);
        productos.subscribe();
        model.addAttribute("titulo","Listado de Productos DataDriver");

        model.addAttribute("productos",productos);
        return "listar-chunked";
    }
}
