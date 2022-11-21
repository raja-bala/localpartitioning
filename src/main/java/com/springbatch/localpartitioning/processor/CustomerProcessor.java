package com.springbatch.localpartitioning.processor;

import com.springbatch.localpartitioning.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        customer.setFirstName(customer.getFirstName().toUpperCase());
        return customer;
    }
}
