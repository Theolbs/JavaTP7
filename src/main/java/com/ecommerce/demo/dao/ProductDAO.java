package com.ecommerce.demo.dao;
import com.ecommerce.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {
    Product findById(int id);

    List<Product> findByPrixGreaterThan(int prixLimit);

    List<Product> findByNomLike(String recherche);

    List<Product> findAllByOrderByNom();

    @Query("SELECT p FROM Product p WHERE p.prix > :prixLimit")
    List<Product> chercherUnProduitCher(@Param("prixLimit") int prix);
}
