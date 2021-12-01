package com.springboot.webflux.app;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringbootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootWebfluxApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApplication.class, args);
	}

	@Autowired
	ProductoRepository productoRepository;

	//Esta MongoTemplate nos permite gestionar la coleccion
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public void run(String... args) throws Exception {
		//Borramos los datos anteriores para tenerlos de prueba cada inicio de la app
		log.warn("Inicializando Coleccion productos");
		//Como recordatorio cada Objeto reactivo debe de suscribirse para ejecutar el contenido
		reactiveMongoTemplate.dropCollection("productos").subscribe();
		//Generamos e insertamos datos de ejemplo
		Flux.just(
				new Producto("Nintendo Switch OLED",279.99),
				new Producto("Sony Camara HD",150.75),
				new Producto("TV Panasonic LCD",87.25),
				new Producto("Play Station 5",499.99)
		)
		.flatMap(p -> {
			p.setCreateAt(new Date());
			return productoRepository.save(p);
		})
		.subscribe(producto -> log.warn("Init Example Producto: ".concat(producto.getNombre())));
	}
}
