package com.example.softwareengineer.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_tags", uniqueConstraints = @UniqueConstraint(name = "uk_tag_name", columnNames = "name"))
public class CustomerTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @OneToMany(mappedBy = "tag")
    private Set<CustomerTagLink> customerLinks = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<CustomerTagLink> getCustomerLinks() { return customerLinks; }
    public void setCustomerLinks(Set<CustomerTagLink> customerLinks) { this.customerLinks = customerLinks; }
}
