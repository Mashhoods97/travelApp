package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Customer;
import com.example.TP.model.User;
import com.example.TP.payload.request.CustomerRequest;
import com.example.TP.payload.response.DestinationResponse;
import com.example.TP.payload.response.CustomerResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.CustomerRepo;
import com.example.TP.repository.UserRepo;
import com.example.TP.utils.BeanUtilsCustom;
import com.example.TP.utils.GeneralException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CustomerService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomerRepo customerRepo;

    public ResponseModel<?> createCustomer(CustomerRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Customer customer = modelMapper.map(entity, Customer.class);
            BeanUtilsCustom.copySelectedProperties(creator, customer, "businessId");
            // 7. Save and prepare response
            Customer savedCustomer = customerRepo.save(customer);
            CustomerResponse response = modelMapper.map(savedCustomer, CustomerResponse.class);

            log.info("Customer created by {} (ID: {}) - New Customer ID: {}",
                    creator.getUsername(), creator.getId(), savedCustomer.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<CustomerResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> getById(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });


            Customer entityBefore = customerRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            CustomerResponse response = modelMapper.map(entityBefore, CustomerResponse.class);

            log.info("Customer retrieved by {} (ID: {}) - Customer ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<DestinationResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }
    public ResponseModel<?> updateCustomer(CustomerRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Customer entityBefore = customerRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            Customer customer = modelMapper.map(entity, Customer.class);
            BeanUtils.copyProperties(customer, entityBefore, "id", "createdBy", "updatedBy", "updatedAt", "createdAt", "businessId");

            entityBefore.setUpdatedBy(creator.getId());
            customerRepo.save(entityBefore);
            CustomerResponse response = modelMapper.map(entityBefore, CustomerResponse.class);

            log.info("Customer updated by {} (ID: {}) - Customer ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<CustomerResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> archiveCustomer(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Customer entityBefore = customerRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            entityBefore.setArchive(true);
            entityBefore.setArchivedBy(creator.getId());
            entityBefore.setArchivedAt(new Date());
            customerRepo.save(entityBefore);
            CustomerResponse response = modelMapper.map(entityBefore, CustomerResponse.class);

            log.info("Customer archived by {} (ID: {}) - Customer ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<CustomerResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<PageResponse<CustomerResponse>> getAllCustomers(Pageable pageable,
                                                                   UserDetails userDetails,
                                                                   String name) {
        try {
            Optional<User> userOptional = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false);
            if (userOptional.isEmpty()) {
                log.error("Access Denied - User not found for username: {}", userDetails.getUsername());
                return ResponseModel.failedPage(ResponseEnum.FORBIDDEN.getStatus(),
                        "Access Denied - User not found.");
            }

            User currentUser = userOptional.get();

            // Validate pagination
            if (pageable.getPageNumber() < 0 || pageable.getPageSize() < 1) {
                throw new GeneralException("Invalid page number or page size", GeneralException.HTTP_BAD_REQUEST);
            }

            // Get filtered users from database
            Page<Customer> customersPage = customerRepo.findByBusinessIdAndFilters(
                    currentUser.getBusinessId(),
                    name != null ? name.toLowerCase() : null,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<CustomerResponse> customersResponsePage = customersPage.map(customer -> modelMapper.map(customer, CustomerResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", customersResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }

    public ResponseModel<?> getCustomersIdNameMap(UserDetails userDetails, String nameSearch) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            List<Customer> customers;

            if (nameSearch != null && !nameSearch.trim().isEmpty()) {
                customers = customerRepo.findByBusinessIdAndFirstNameContainingIgnoreCaseAndArchiveFalse(creator.getBusinessId(), nameSearch.trim());
            } else {
                customers = customerRepo.findByBusinessIdAndArchiveFalse(creator.getBusinessId());
            }

            Map<Long, String> customersMap = customers.stream()
                    .collect(Collectors.toMap(
                            Customer::getId,
                            Customer::getFirstName,
                            (existing, replacement) -> existing
                    ));

            return new ResponseModel<>().success(ResponseEnum.CREATED.getStatus(), customersMap);

        } catch (Exception e) {
            log.error("Error retrieving customers map: {}", e.getMessage(), e);
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to retrieve customers");
        }
    }

    private ResponseModel<?> successResponse(CustomerResponse data) {
        return new ResponseModel<CustomerResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create Customer");
    }
}
