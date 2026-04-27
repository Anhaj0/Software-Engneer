package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "customer")
@NamedEntityGraph(
    name = "Customer.detail",
    attributeNodes = {
        @NamedAttributeNode("mobiles"),
        @NamedAttributeNode(value = "addresses", subgraph = "address.details"),
        @NamedAttributeNode("familyMembers")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "address.details",
            attributeNodes = {
                @NamedAttributeNode("city"),
                @NamedAttributeNode("country")
            }
        )
    }
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @Column(name = "nic_number", unique = true)
    private String nicNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "customer_mobile", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "mobile_number")
    private Set<String> mobiles = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CustomerAddress> addresses = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "customer_family",
        joinColumns = @JoinColumn(name = "parent_customer_id"),
        inverseJoinColumns = @JoinColumn(name = "child_customer_id")
    )
    private Set<Customer> familyMembers = new HashSet<>();

    public Customer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }

    public Set<String> getMobiles() { return mobiles; }
    public void setMobiles(Set<String> mobiles) { this.mobiles = mobiles; }

    public Set<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(Set<CustomerAddress> addresses) {
        this.addresses.clear();
        if (addresses != null) {
            addresses.forEach(a -> {
                a.setCustomer(this);
                this.addresses.add(a);
            });
        }
    }

    public void addAddress(CustomerAddress address) {
        address.setCustomer(this);
        this.addresses.add(address);
    }

    public Set<Customer> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(Set<Customer> familyMembers) { this.familyMembers = familyMembers; }

}
