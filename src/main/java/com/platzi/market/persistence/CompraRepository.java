package com.platzi.market.persistence;

import com.platzi.market.domain.Purchase;
import com.platzi.market.domain.repository.PurchaseRepository;
import com.platzi.market.persistence.crud.CompraCrudRepository;
import com.platzi.market.persistence.entity.Compra;
import com.platzi.market.persistence.entity.ComprasProductoPK;
import com.platzi.market.persistence.mapper.PurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompraRepository implements PurchaseRepository {
    /*
    * @Autowired : Sirve para no darle un valor explicito a la variable, sino que indicamos
    * a traves de esta directis que el framework Spring lo hara por nosotros
    * */
    @Autowired
    private CompraCrudRepository compraCrudRepository;

    @Autowired
    private PurchaseMapper mapper;

    @Override
    public List<Purchase> getAll() {
        return mapper.toPurchases((List<Compra>) compraCrudRepository.findAll());
    }

    @Override
    public Optional<List<Purchase>> getByClient(String clientId) {
        return compraCrudRepository.findByIdCliente(clientId)
                /*
                * El metodo map sirve para operar con lo que sea que halla en el Optional
                * si no hay nada dentro del Optional no se ejecuta
                * */
                .map(compras -> mapper.toPurchases(compras));
    }

    @Override
    public Purchase save(Purchase purchase) {
        Compra compra = mapper.toCompra(purchase);

        /*
        * Tenemos que garantizar que estos datos se guarden en cascada, para ello, lo que tenemos que estar
        * seguros de que Compra conoce los Productos y los Productos conocen a que compra pertenecen
        * */

        compra.getProductos().forEach(productos -> productos.setCompra(compra));

        return mapper.toPurchase(compraCrudRepository.save(compra));
    }
}
