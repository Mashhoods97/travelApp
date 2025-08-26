package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Business;
import com.example.TP.model.Role;
import com.example.TP.model.User;
import com.example.TP.payload.request.UserRequest;
import com.example.TP.payload.response.BusinessResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.payload.response.UserResponse;
import com.example.TP.repository.BusinessRepo;
import com.example.TP.repository.RoleRepo;
import com.example.TP.repository.UserRepo;
import com.example.TP.utils.Constants;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.TP.utils.Constants.UserType.*;
import static org.springframework.http.HttpStatus.FAILED_DEPENDENCY;

@Service
@Log4j2
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BusinessRepo businessRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    PasswordEncoder encoder;

    public ResponseModel<?> createUser(UserRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            // 2. Check email uniqueness
            if (userRepo.existsByEmailAndArchiveFalse(entity.getEmail())) {
                log.warn("Duplicate email: {}", entity.getEmail());
                return conflictResponse();
            }
            // 2. Check username uniqueness
            if (userRepo.existsByUsernameAndArchiveFalse(entity.getUsername())) {
                log.warn("Duplicate username: {}", entity.getUsername());
                return conflictResponse();
            }

            // 3. Validate role hierarchy
            try {
                verifyCreateUserRequest(entity.getType(), userDetails);
            } catch (SecurityException ex) {
                return forbiddenResponse(ex.getMessage());
            }

            // 4. Create and configure new user
            User newUser = modelMapper.map(entity, User.class);
            newUser.setPassword(encoder.encode(entity.getPassword()));

            // 5. Handle role assignment
            resolveUserRole(entity, newUser);

            // 6. Handle business assignment
            BusinessResponse businessResponse = handleBusinessAssignment(entity, creator, newUser);

            // 7. Save and prepare response
            User savedUser = userRepo.save(newUser);
            UserResponse response = modelMapper.map(savedUser, UserResponse.class);
            response.setBusinessResponse(businessResponse);

            log.info("User created by {} (ID: {}) - New user ID: {}",
                    creator.getUsername(), creator.getId(), savedUser.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<UserResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> updateUser(UserRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            // 2. Check email uniqueness
            if (userRepo.existsByEmailAndIdNot(entity.getEmail(), id)) {
                log.warn("Duplicate email: {}", entity.getEmail());
                return conflictResponse();
            }

            // 3. Validate role hierarchy
            try {
                verifyCreateUserRequest(entity.getType(), userDetails);
            } catch (SecurityException ex) {
                return forbiddenResponse(ex.getMessage());
            }

            User entityBefore = userRepo.findOptionalByIdAndArchiveFalse(id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            User user = modelMapper.map(entity, User.class);
            BeanUtils.copyProperties(user, entityBefore, "id", "createdBy", "updatedBy", "updatedAt", "createdAt", "businessId", "password");

            entityBefore.setUpdatedBy(creator.getId());
            userRepo.save(entityBefore);
            UserResponse response = modelMapper.map(entityBefore, UserResponse.class);

            log.info("User updated by {} (ID: {}) - user ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<UserResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> archiveUser(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            User entityBefore = userRepo.findOptionalByIdAndArchiveFalse(id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            entityBefore.setArchive(true);
            entityBefore.setArchivedBy(creator.getId());
            entityBefore.setArchivedAt(new Date());
            userRepo.save(entityBefore);
            UserResponse response = modelMapper.map(entityBefore, UserResponse.class);

            log.info("User archived by {} (ID: {}) - user ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<UserResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<PageResponse<UserResponse>> getAllUsers(Pageable pageable,
                                                                 UserDetails userDetails,
                                                                 String name,
                                                                 String email,
                                                                 String phone) {
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
            Page<User> usersPage = userRepo.findByBusinessIdAndFilters(
                    currentUser.getBusinessId(),
                    name != null ? name.toLowerCase() : null,
                    email != null ? email.toLowerCase() : null,
                    phone,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<UserResponse> userResponsePage = usersPage.map(user -> modelMapper.map(user, UserResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", userResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }


// Helper Methods ==============================================

    private BusinessResponse handleBusinessAssignment(UserRequest request, User creator, User newUser) {
        if (request.getType() == HEAD) {
            if (request.getBusinessRequest() == null) {
                throw new GeneralException("Business data required for HEAD user", FAILED_DEPENDENCY);
            }

            Business business = modelMapper.map(request.getBusinessRequest(), Business.class);
            business.setActive(true);
            Business savedBusiness = businessRepo.save(business);

            newUser.setBusinessId(savedBusiness.getId());
            newUser.setActive(true);

            return modelMapper.map(savedBusiness, BusinessResponse.class);
        } else {
            newUser.setBusinessId(creator.getBusinessId());
            newUser.setActive(creator.isActive());
            return null; // No new business created
        }
    }

    private void resolveUserRole(UserRequest request, User newUser) {
        if (request.getRoleId() == null || request.getRoleId() == 0) {
            setUserRole(request, newUser); // Your existing role-setting logic
        } else {
            Role role = roleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new GeneralException(
                            "Role not found: " + request.getRoleId(),
                            GeneralException.HTTP_NOT_FOUND));
            newUser.setRole(role);
        }
    }

    public void verifyCreateUserRequest(Integer type, UserDetails userDetails) {
        Optional<User> userOptional = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false);
        if (userOptional.isEmpty()) {
            throw new SecurityException("Unauthorized: User not found");
        }
        User user = userOptional.get();
        if (type == Constants.UserType.OWNER) {
            throw new SecurityException("Unauthorized: Access required");
        } else if (type == 0) {
            throw new SecurityException("Missing: userType value");
        }
        int userType = user.getType();
        int newUserType = type;
        boolean isAuthorized = switch (userType) {
            case -1 -> newUserType == RESELLER || newUserType == HEAD;
            case 100 -> newUserType == HEAD;
            case 110 -> List.of(MANAGER, SALES_MANAGER, CLIENT, CUSTOMER).contains(newUserType);
            case 120 -> List.of(SALES_MANAGER, CLIENT, CUSTOMER).contains(newUserType);
            case 130 -> List.of(CLIENT, CUSTOMER).contains(newUserType);
            case 150 -> false;
            case 160 -> newUserType == CUSTOMER;
            default -> false;
        };

        if (!isAuthorized) {
            throw new SecurityException("Unauthorized: Access required");
        }
    }

    public void setUserRole(UserRequest entity, User newUser) {
        long roleId;
        if (entity.getType() == Constants.UserType.RESELLER) {
            roleId = 2L;
        } else if (entity.getType() == HEAD) {
            roleId = 3L;
        } else if (entity.getType() == MANAGER) {
            roleId = 4L;
        } else if (entity.getType() == SALES_MANAGER) {
            roleId = 5L;
        } else if (entity.getType() == CLIENT) {
            roleId = 6L;
        } else if (entity.getType() == CUSTOMER) {
            roleId = 7L;
        } else {
            throw new IllegalArgumentException("Unknown user type: " + entity.getType());
        }
        Optional<Role> roleOptional = roleRepo.findById(roleId);
        if (roleOptional.isPresent()) {
            newUser.setRole(roleOptional.get());
        } else {
            throw new GeneralException("Role with ID " + roleId + " not found", GeneralException.HTTP_NOT_FOUND);
        }
    }

    // Response helpers (reusable across the application)
    private ResponseModel<?> successResponse(UserResponse data) {
        return new ResponseModel<UserResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> conflictResponse() {
        return new ResponseModel<>().failed(ResponseEnum.CONFLICT.getStatus(), "Email already exists");
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create user");
    }

    public Optional<User> findOptionalByUsernameAndActive(String name) {
        return userRepo.findOptionalByUsernameAndActive(name, true);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
