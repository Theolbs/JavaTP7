package com.ecommerce.demo.web.controller;

import com.ecommerce.demo.dao.ProductDAO;
import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.web.exceptions.ProduitGratuitException;
import com.ecommerce.demo.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Api("API pour les opérations de Théo")
public class ProductController {
    @Autowired
    private ProductDAO productDao;

    //Récupérer la liste des produits
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre =
                SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new
                SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new
                MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
        Product produit =  productDao.findById(id);
        if(produit==null) throw new ProduitIntrouvableException("Le produit" + id +" n'existe pas.");
        return produit;
    }

    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère les produits et calcule leur marge")
    @GetMapping(value = "/AdminProduits")
    public Map<String, Integer> calculerMargeProduit() {
        List<Product> recupererlesProduit = productDao.findAll();
        Map<String, Integer> lMap = new HashMap<>();
        for(Product p: recupererlesProduit) {
            lMap.put(p.toString(), p.getPrix() - p.getPrixAchat());
        }
        return lMap;
    }

    //Récupérer un produit par son Id
    @ApiOperation(value = "Trie les produits")
    @GetMapping(value = "/OrdreProduits")
    public List<Product> trierProduitsParOrdreAlphabetique() {
        List<Product> recupererlesProduit = productDao.findAllByOrderByNom();
        return recupererlesProduit;
    }

    /*@GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testeDeRequetesPrix(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(600);
    }*/

    @GetMapping(value = "test/produits/{recherche}")
    public List<Product> testeDeRequetesNom(@PathVariable String recherche) {
        return productDao.findByNomLike("%" + recherche + "%");
    }


    //ajouter un produit
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        if(product.getPrix() == 0) throw new ProduitGratuitException("Impossible d'ajouter un produit gratuit.");
        Product productAdded = productDao.save(product);
        if (productAdded == null)
            return ResponseEntity.noContent().build();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.delete(productDao.findById(id));
    }

    @PutMapping (value = "/Produits")
    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }
}
