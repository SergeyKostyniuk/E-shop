package com.social.eshop.domain;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
=======
>>>>>>> with_entities
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
<<<<<<< HEAD
import java.util.HashSet;
import java.util.Set;
=======
>>>>>>> with_entities
import java.util.Objects;

/**
 * A Consignment.
 */
@Entity
@Table(name = "consignment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "consignment")
public class Consignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "price", precision=10, scale=2, nullable = false)
    private BigDecimal price;

<<<<<<< HEAD
    @OneToMany(mappedBy = "consignment")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Products> products = new HashSet<>();
=======
    @ManyToOne
    private Products products;
>>>>>>> with_entities

    @ManyToOne
    private Storage storage;

    @ManyToOne
    private Producers producers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Consignment price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

<<<<<<< HEAD
    public Set<Products> getProducts() {
        return products;
    }

    public Consignment products(Set<Products> products) {
=======
    public Products getProducts() {
        return products;
    }

    public Consignment products(Products products) {
>>>>>>> with_entities
        this.products = products;
        return this;
    }

<<<<<<< HEAD
    public Consignment addProducts(Products products) {
        this.products.add(products);
        products.setConsignment(this);
        return this;
    }

    public Consignment removeProducts(Products products) {
        this.products.remove(products);
        products.setConsignment(null);
        return this;
    }

    public void setProducts(Set<Products> products) {
=======
    public void setProducts(Products products) {
>>>>>>> with_entities
        this.products = products;
    }

    public Storage getStorage() {
        return storage;
    }

    public Consignment storage(Storage storage) {
        this.storage = storage;
        return this;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Producers getProducers() {
        return producers;
    }

    public Consignment producers(Producers producers) {
        this.producers = producers;
        return this;
    }

    public void setProducers(Producers producers) {
        this.producers = producers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Consignment consignment = (Consignment) o;
        if (consignment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), consignment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Consignment{" +
            "id=" + getId() +
            ", price='" + getPrice() + "'" +
            "}";
    }
}
