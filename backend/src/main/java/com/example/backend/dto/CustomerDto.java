package com.example.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String nicNumber;
    private Set<String> mobiles;
    private List<AddressDto> addresses;
    private Set<Long> familyMemberIds;
    
    // For response, we might need basic info of family
    private List<CustomerDto> familyMembers;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }
    public Set<String> getMobiles() { return mobiles; }
    public void setMobiles(Set<String> mobiles) { this.mobiles = mobiles; }
    public List<AddressDto> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDto> addresses) { this.addresses = addresses; }
    public Set<Long> getFamilyMemberIds() { return familyMemberIds; }
    public void setFamilyMemberIds(Set<Long> familyMemberIds) { this.familyMemberIds = familyMemberIds; }
    public List<CustomerDto> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<CustomerDto> familyMembers) { this.familyMembers = familyMembers; }
}
