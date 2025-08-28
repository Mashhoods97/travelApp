package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.ItineraryRequest;
import com.example.TP.payload.response.ItineraryResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.ItineraryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/itineraries")
@Log4j2
public class ItineraryController {
    @Autowired
    private ItineraryService itineraryService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody ItineraryRequest itineraryRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return itineraryService.createItinerary(itineraryRequest, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody ItineraryRequest itineraryRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return itineraryService.updateItinerary(itineraryRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return itineraryService.archiveItinerary(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<ItineraryResponse>> getAllItinerarys(Pageable pageable,
                                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                                   @RequestParam(name = "title", required = false) String title,
                                                                   @RequestParam(name = "packageId", required = false) Long packageId) {
        return itineraryService.getAllItineraries(pageable, userDetails, title, packageId);
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
