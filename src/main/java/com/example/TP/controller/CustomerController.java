package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.CustomerRequest;
import com.example.TP.payload.response.CustomerResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/customers")
@Log4j2
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody CustomerRequest customerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return customerService.createCustomer(customerRequest, userDetails);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return customerService.getById(id, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody CustomerRequest customerRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return customerService.updateCustomer(customerRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return customerService.archiveCustomer(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<CustomerResponse>> getAllCustomers(Pageable pageable,
                                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestParam(name = "name", required = false) String name) {
        return customerService.getAllCustomers(pageable, userDetails, name);
    }

    @GetMapping("/get")
    public ResponseModel<?> getCustomersMap(
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return customerService.getCustomersIdNameMap(userDetails, name);
    }

    @ExceptionHandler(Exception.class)
    public ResponseModel<?> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        if (e.getClass().getSimpleName().equalsIgnoreCase("AccessDeniedException")) {
            return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), "AccessDenied");
        } else {
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage());
        }
    }
}
