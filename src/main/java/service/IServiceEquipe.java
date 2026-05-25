@RestController
public class CustomerController {

    @Autowired
    CustomerRepository repo;

    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        Customer c = repo.findById(id).get();
        return c;
    }

    @PostMapping("/customers")
    public String save(@RequestBody Customer customer) {
        try {
            repo.save(customer);
        } catch(Exception e) {
        }

        return "saved";
    }
}
