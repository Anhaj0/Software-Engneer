package com.example.backend.service;

import com.example.backend.dto.AddressDto;
import com.example.backend.dto.CustomerDto;
import com.example.backend.entity.City;
import com.example.backend.entity.Country;
import com.example.backend.entity.Customer;
import com.example.backend.entity.CustomerAddress;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.CountryRepository;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::mapToDto);
    }

    public CustomerDto getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = new Customer();
        mapToEntity(dto, customer);
        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        mapToEntity(dto, customer);
        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    private void mapToEntity(CustomerDto dto, Customer customer) {
        customer.setName(dto.getName());
        customer.setDob(dto.getDob());
        customer.setNicNumber(dto.getNicNumber());
        customer.setMobiles(dto.getMobiles() != null ? new HashSet<>(dto.getMobiles()) : new HashSet<>());

        if (dto.getAddresses() != null) {
            Set<CustomerAddress> addresses = new HashSet<>();
            for (AddressDto adto : dto.getAddresses()) {
                CustomerAddress address = new CustomerAddress();
                address.setLine1(adto.getLine1());
                address.setLine2(adto.getLine2());
                
                if (adto.getCityId() != null) {
                    address.setCity(cityRepository.findById(adto.getCityId()).orElse(null));
                }
                if (adto.getCountryId() != null) {
                    address.setCountry(countryRepository.findById(adto.getCountryId()).orElse(null));
                }
                addresses.add(address);
            }
            customer.setAddresses(addresses);
        } else {
            customer.setAddresses(new HashSet<>());
        }

        if (dto.getFamilyMemberIds() != null) {
            List<Customer> families = customerRepository.findAllById(dto.getFamilyMemberIds());
            customer.setFamilyMembers(new HashSet<>(families));
        } else {
            customer.setFamilyMembers(new HashSet<>());
        }
    }

    private CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setDob(customer.getDob());
        dto.setNicNumber(customer.getNicNumber());
        dto.setMobiles(customer.getMobiles());
        
        List<AddressDto> addressDtos = customer.getAddresses().stream().map(a -> {
            AddressDto ad = new AddressDto();
            ad.setId(a.getId());
            ad.setLine1(a.getLine1());
            ad.setLine2(a.getLine2());
            if (a.getCity() != null) {
                ad.setCityId(a.getCity().getId());
                ad.setCityName(a.getCity().getName());
            }
            if (a.getCountry() != null) {
                ad.setCountryId(a.getCountry().getId());
                ad.setCountryName(a.getCountry().getName());
            }
            return ad;
        }).collect(Collectors.toList());
        dto.setAddresses(addressDtos);

        Set<Long> familyIds = customer.getFamilyMembers().stream().map(Customer::getId).collect(Collectors.toSet());
        dto.setFamilyMemberIds(familyIds);

        return dto;
    }
}
