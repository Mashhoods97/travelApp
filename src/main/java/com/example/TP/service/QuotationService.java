package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Quotation;
import com.example.TP.model.User;
import com.example.TP.payload.request.QuotationRequest;
import com.example.TP.payload.response.DestinationResponse;
import com.example.TP.payload.response.QuotationResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.QuotationRepo;
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
public class QuotationService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuotationRepo quotationRepo;

    public ResponseModel<?> createQuotation(QuotationRequest entity, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Quotation quotation = modelMapper.map(entity, Quotation.class);
            BeanUtilsCustom.copySelectedProperties(creator, quotation, "businessId");
            // 7. Save and prepare response
            Quotation savedQuotation = quotationRepo.save(quotation);
            QuotationResponse response = modelMapper.map(savedQuotation, QuotationResponse.class);

            log.info("Quotation created by {} (ID: {}) - New Quotation ID: {}",
                    creator.getUsername(), creator.getId(), savedQuotation.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<QuotationResponse>().failed(e.getHttpStatus().value(), e.getMessage());
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


            Quotation entityBefore = quotationRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            QuotationResponse response = modelMapper.map(entityBefore, QuotationResponse.class);

            log.info("Quotation retrieved by {} (ID: {}) - Quotation ID: {}",
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
    public ResponseModel<?> updateQuotation(QuotationRequest entity, Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Quotation entityBefore = quotationRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            Quotation quotation = modelMapper.map(entity, Quotation.class);
            BeanUtils.copyProperties(quotation, entityBefore, "id", "createdBy", "updatedBy", "updatedAt", "createdAt", "businessId");

            entityBefore.setUpdatedBy(creator.getId());
            quotationRepo.save(entityBefore);
            QuotationResponse response = modelMapper.map(entityBefore, QuotationResponse.class);

            log.info("Quotation updated by {} (ID: {}) - Quotation ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<QuotationResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<?> archiveQuotation(Long id, UserDetails userDetails) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            Quotation entityBefore = quotationRepo.findOptionalByBusinessIdAndIdAndArchiveFalse(creator.getBusinessId(), id)
                    .orElseThrow(() -> {
                        log.error("Entity with ID '{}' not found for updating by user '{}'", id, userDetails.getUsername());
                        return new GeneralException("Error updating entity :: Entity not found for updating.", HttpStatus.NOT_FOUND);
                    });

            entityBefore.setArchive(true);
            entityBefore.setArchivedBy(creator.getId());
            entityBefore.setArchivedAt(new Date());
            quotationRepo.save(entityBefore);
            QuotationResponse response = modelMapper.map(entityBefore, QuotationResponse.class);

            log.info("Quotation archived by {} (ID: {}) - Quotation ID: {}",
                    creator.getUsername(), creator.getId(), entityBefore.getId());

            return successResponse(response);

        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            return forbiddenResponse(e.getMessage());
        } catch (GeneralException e) {
            log.error("Business error: {}", e.getMessage());
            return new ResponseModel<QuotationResponse>().failed(e.getHttpStatus().value(), e.getMessage());
        } catch (Exception e) {
            log.error("System error: {}", e.getMessage(), e);
            return internalErrorResponse();
        }
    }

    public ResponseModel<PageResponse<QuotationResponse>> getAllQuotations(Pageable pageable,
                                                                   UserDetails userDetails,
                                                                   String title) {
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
            Page<Quotation> quotationsPage = quotationRepo.findByBusinessIdAndFilters(
                    currentUser.getBusinessId(),
                    title != null ? title.toLowerCase() : null,
                    pageable
            );

            // Convert to UserResponse using ModelMapper
            Page<QuotationResponse> quotationsResponsePage = quotationsPage.map(quotation -> modelMapper.map(quotation, QuotationResponse.class));

            log.info("Users retrieved by user '{}' with business ID '{}'",
                    userDetails.getUsername(), currentUser.getBusinessId());

            return ResponseModel.successPage(ResponseEnum.FOUND.getStatus(), "Entities Retrieved Successfully", quotationsResponsePage);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred during user retrieval: {}", e.getMessage());
            return ResponseModel.failedPage(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(),
                    "Error retrieving users: " + e.getMessage());
        } catch (GeneralException e) {
            log.error("GeneralException: {}", e.getMessage());
            return ResponseModel.failedPage(e.getHttpStatus().value(), e.getMessage());
        }
    }

    public ResponseModel<?> getQuotationsIdNameMap(UserDetails userDetails, String nameSearch) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            List<Quotation> quotations;

            if (nameSearch != null && !nameSearch.trim().isEmpty()) {
                quotations = quotationRepo.findByBusinessIdAndTitleContainingIgnoreCaseAndArchiveFalse(creator.getBusinessId(), nameSearch.trim());
            } else {
                quotations = quotationRepo.findByBusinessIdAndArchiveFalse(creator.getBusinessId());
            }

            Map<Long, String> quotationsMap = quotations.stream()
                    .collect(Collectors.toMap(
                            Quotation::getId,
                            Quotation::getTitle,
                            (existing, replacement) -> existing
                    ));

            return new ResponseModel<>().success(ResponseEnum.CREATED.getStatus(), quotationsMap);

        } catch (Exception e) {
            log.error("Error retrieving quotations map: {}", e.getMessage(), e);
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to retrieve quotations");
        }
    }

    private ResponseModel<?> successResponse(QuotationResponse data) {
        return new ResponseModel<QuotationResponse>().success(ResponseEnum.CREATED.getStatus(), data);
    }

    private ResponseModel<?> forbiddenResponse(String message) {
        return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), message);
    }

    private ResponseModel<?> internalErrorResponse() {
        return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to create Quotation");
    }
}
