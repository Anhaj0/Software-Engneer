package com.example.softwareengineer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_tag_links", uniqueConstraints = @UniqueConstraint(name = "uk_customer_tag", columnNames = {"customer_id", "tag_id"}))
public class CustomerTagLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private CustomerTag tag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public CustomerTag getTag() { return tag; }
    public void setTag(CustomerTag tag) { this.tag = tag; }
}
