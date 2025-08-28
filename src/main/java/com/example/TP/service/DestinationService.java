package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Destination;
import com.example.TP.model.User;
import com.example.TP.payload.request.DestinationRequest;
import com.example.TP.payload.response.DestinationResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.DestinationRepo;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DestinationService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DestinationRepo destinationRepo;

    public ResponseModel<?> createDestination(DestinationRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Destination destination = modelMapper.map(entity, Destination.class);

            // 7. Save and prepare response
            Destination savedDestination = destinationRepo.save(destination);
            DestinationResponse response = modelMapper.map(savedDestination, DestinationResponse.class);

            log.info("Destination created by {} (ID: {}) - New Destination ID: {}",
                    creator.getUsername(), creator.getId(), savedDestination.getId());

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

    public ResponseModel<?> updateDestination(DestinationRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });


            Destination entityBefore = destinationRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            Destination destination = modelMapper.map(entity, Destination.class);
            BeanUtils.copyProperties(destination, entityBefore, "id", "createdBy", "updatedBy", "updatedAt", "createdAt", "businessId", "password");

            entityBefore.setUpdatedBy(creator.getId());
            destinationRepo.save(entityBefore);
            DestinationResponse response = modelMapper.map(entityBefore, DestinationResponse.class);

            log.info("Destination updated by {} (ID: {}) - Destination ID: {}",
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

    public ResponseModel<?> archiveDestination(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Destination entityBefore = destinationRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            entityBefore.setArchive(true);
            entityBefore.setArchivedBy(creator.getId());
            entityBefore.setArchivedAt(new Date());
            destinationRepo.save(entityBefore);
            DestinationResponse response = modelMapper.map(entityBefore, DestinationResponse.class);

            log.info("Destination archived by {} (ID: {}) - Destination ID: {}",
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

    public ResponseModel<PageResponse<DestinationResponse>> getAllDestinations(Pageable pageable,
                                                                               UserDetails userDetails,
                                                                               String name,
                                                                               String country,
                                                                               String slug) {
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
            Page<Destination> destinationsPage = destinationRepo.findByBusinessIdAndFilters(
                    currentUser.getBusinessId(),
                    name != null ? name.toLowerCase() : null,
                    country != null ? country.toLowerCase() : null,
                    slug != null ? slug.toLowerCase() : null,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<DestinationResponse> destinationsResponsePage = destinationsPage.map(destination -> modelMapper.map(destination, DestinationResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", destinationsResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }

    public ResponseModel<?> getDestinationsIdNameMap(UserDetails userDetails, String nameSearch) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            List<Destination> destinations;

            if (nameSearch != null && !nameSearch.trim().isEmpty()) {
                destinations = destinationRepo.findByBusinessIdAndNameContainingIgnoreCaseAndArchiveFalse(creator.getBusinessId(), nameSearch.trim());
            } else {
                destinations = destinationRepo.findByBusinessIdAndArchiveFalse(creator.getBusinessId());
            }

            Map<Long, String> destinationsMap = destinations.stream()
                    .collect(Collectors.toMap(
                            Destination::getId,
                            Destination::getName,
                            (existing, replacement) -> existing
                    ));

            return new ResponseModel<>().success(ResponseEnum.CREATED.getStatus(), destinationsMap);

        } catch (Exception e) {
            log.error("Error retrieving destinations map: {}", e.getMessage(), e);
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to retrieve destinations");
        }
    }

    private ResponseModel<?> successResponse(DestinationResponse data) {
        return new ResponseModel<DestinationResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create Destination");
    }
}
