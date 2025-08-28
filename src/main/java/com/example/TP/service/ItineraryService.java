package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Itinerary;
import com.example.TP.model.User;
import com.example.TP.payload.request.ItineraryRequest;
import com.example.TP.payload.response.ItineraryResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.ItineraryRepo;
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
public class ItineraryService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ItineraryRepo itineraryRepo;

    public ResponseModel<?> createItinerary(ItineraryRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Itinerary itinerary = modelMapper.map(entity, Itinerary.class);

            // 7. Save and prepare response
            Itinerary savedItinerary = itineraryRepo.save(itinerary);
            ItineraryResponse response = modelMapper.map(savedItinerary, ItineraryResponse.class);

            log.info("Itinerary created by {} (ID: {}) - New Itinerary ID: {}",
                    creator.getUsername(), creator.getId(), savedItinerary.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<ItineraryResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> updateItinerary(ItineraryRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });


            Itinerary entityBefore = itineraryRepo.findOptionalById(id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            Itinerary itinerary = modelMapper.map(entity, Itinerary.class);
            BeanUtils.copyProperties(itinerary, entityBefore, "id");

            itineraryRepo.save(entityBefore);
            ItineraryResponse response = modelMapper.map(entityBefore, ItineraryResponse.class);

            log.info("Itinerary updated by {} (ID: {}) - Itinerary ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<ItineraryResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> archiveItinerary(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Itinerary entityBefore = itineraryRepo.findOptionalById(id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            itineraryRepo.delete(entityBefore);
            ItineraryResponse response = modelMapper.map(entityBefore, ItineraryResponse.class);

            log.info("Itinerary archived by {} (ID: {}) - Itinerary ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<ItineraryResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<PageResponse<ItineraryResponse>> getAllItineraries(Pageable pageable,
                                                                            UserDetails userDetails,
                                                                            String title,
                                                                            Long packageId) {
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
            Page<Itinerary> itinerarysPage = itineraryRepo.findByFilters(
                    title != null ? title.toLowerCase() : null,
                    packageId,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<ItineraryResponse> itinerarysResponsePage = itinerarysPage.map(itinerary -> modelMapper.map(itinerary, ItineraryResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", itinerarysResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }

    private ResponseModel<?> successResponse(ItineraryResponse data) {
        return new ResponseModel<ItineraryResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create Itinerary");
    }
}
