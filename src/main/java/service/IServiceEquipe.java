import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    @Autowired
    CustomerRepository repo;

    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        Customer c = repo.findById(id).orElse(null);
        return c;
    }

    @PostMapping("/customers")
    public Customer save(@RequestBody Customer customer) {
        try {
            return repo.save(customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save customer", e);
        }
    }
}