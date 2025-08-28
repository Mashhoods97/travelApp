package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Package;
import com.example.TP.model.User;
import com.example.TP.payload.request.PackageRequest;
import com.example.TP.payload.response.PackageResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.PackageRepo;
import com.example.TP.repository.UserRepo;
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
import java.util.Optional;

@Service
@Log4j2
public class PackageService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PackageRepo packageRepo;

    public ResponseModel<?> createPackage(PackageRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Package aPackage = modelMapper.map(entity, Package.class);

            // 7. Save and prepare response
            Package savedPackage = packageRepo.save(aPackage);
            PackageResponse response = modelMapper.map(savedPackage, PackageResponse.class);

            log.info("Package created by {} (ID: {}) - New Package ID: {}",
                    creator.getUsername(), creator.getId(), savedPackage.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<PackageResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> updatePackage(PackageRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Package entityBefore = packageRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            Package aPackage = modelMapper.map(entity, Package.class);
            BeanUtils.copyProperties(aPackage, entityBefore, "id", "createdBy", "updatedBy", "updatedAt", "createdAt", "businessId", "password");

            entityBefore.setUpdatedBy(creator.getId());
            packageRepo.save(entityBefore);
            PackageResponse response = modelMapper.map(entityBefore, PackageResponse.class);

            log.info("Package updated by {} (ID: {}) - Package ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<PackageResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> archivePackage(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Package entityBefore = packageRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            entityBefore.setArchive(true);
            entityBefore.setArchivedBy(creator.getId());
            entityBefore.setArchivedAt(new Date());
            packageRepo.save(entityBefore);
            PackageResponse response = modelMapper.map(entityBefore, PackageResponse.class);

            log.info("Package archived by {} (ID: {}) - Package ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<PackageResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<PageResponse<PackageResponse>> getAllPackages(Pageable pageable,
                                                                   UserDetails userDetails,
                                                                   String name,
                                                                   String code) {
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
            Page<Package> packagesPage = packageRepo.findByBusinessIdAndFilters(
                    currentUser.getBusinessId(),
                    name != null ? name.toLowerCase() : null,
                    code != null ? code.toLowerCase() : null,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<PackageResponse> packagesResponsePage = packagesPage.map(aPackage -> modelMapper.map(aPackage, PackageResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", packagesResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }

    private ResponseModel<?> successResponse(PackageResponse data) {
        return new ResponseModel<PackageResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create Package");
    }
}
