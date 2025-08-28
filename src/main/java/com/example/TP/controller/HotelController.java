package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.HotelRequest;
import com.example.TP.payload.response.HotelResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.HotelService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/hotels")
@Log4j2
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody HotelRequest hotelRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return hotelService.createHotel(hotelRequest, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody HotelRequest hotelRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return hotelService.updateHotel(hotelRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return hotelService.archiveHotel(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<HotelResponse>> getAllHotels(Pageable pageable,
                                                                               @AuthenticationPrincipal UserDetails userDetails,
                                                                               @RequestParam(name = "name", required = false) String name,
                                                                               @RequestParam(name = "slug", required = false) String slug) {
        return hotelService.getAllHotels(pageable, userDetails, name, slug);
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
