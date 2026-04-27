package com.example.softwareengineer;

import com.example.softwareengineer.entity.Customer;
import com.example.softwareengineer.repository.CustomerRepository;
import com.example.softwareengineer.service.CustomerUploadService;
import com.example.softwareengineer.upload.CustomerUploadRow;
import com.example.softwareengineer.upload.UploadJobState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerUploadServiceTest {

    @Autowired
    private CustomerUploadService uploadService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void processBatchHandlesDuplicateNicByUpdatingLatest() {
        Customer customer = new Customer();
        customer.setNic("NIC-300");
        customer.setFirstName("Old");
        customer.setLastName("Name");
        customer.setEmail("old@example.com");
        customerRepository.save(customer);

        CustomerUploadRow existing = row("NIC-300", "New", "Name", "new@example.com");
        CustomerUploadRow insert = row("NIC-301", "Alice", "A", "alice@example.com");
        CustomerUploadRow duplicateInFile = row("NIC-301", "Alice2", "B", "alice2@example.com");

        UploadJobState state = new UploadJobState("job-1");
        uploadService.processBatch(List.of(existing, insert, duplicateInFile), state);

        assertThat(customerRepository.findByNic("NIC-300")).get().extracting(Customer::getFirstName).isEqualTo("New");
        assertThat(customerRepository.findByNic("NIC-301")).get().extracting(Customer::getEmail).isEqualTo("alice2@example.com");
        assertThat(state.getSkipped().get()).isEqualTo(1);
        assertThat(state.getUpdated().get()).isEqualTo(1);
        assertThat(state.getInserted().get()).isEqualTo(1);
    }

    private CustomerUploadRow row(String nic, String firstName, String lastName, String email) {
        CustomerUploadRow row = new CustomerUploadRow();
        row.setNic(nic);
        row.setFirstName(firstName);
        row.setLastName(lastName);
        row.setEmail(email);
        return row;
    }
}
