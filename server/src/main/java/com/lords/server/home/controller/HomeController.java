package com.lords.server.home.controller;

import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.home.dto.request.CreateHomeRequest;
import com.lords.server.home.dto.request.HomeUpdateRequest;
import com.lords.server.home.dto.request.HomeUpdateStorage;
import com.lords.server.home.dto.request.ManageMemberRequest;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.service.HomeService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/homes")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @PostMapping
    public ResponseEntity<HomeResponse> createHome(@Valid @RequestBody CreateHomeRequest request, @CurrentUser Long userId) {
        HomeResponse response = homeService.createHome(request.name(), userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<HomeResponse> getHomeDetails(@PathVariable Long homeId, @CurrentUser Long userId) {
        HomeResponse response = homeService.getHome(homeId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<HomeResponse>> getMyHomes(@CurrentUser Long userId,
                                                         @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<HomeResponse> response = homeService.getHomes(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}")
    public ResponseEntity<HomeResponse> updateHome(@PathVariable Long homeId, @Valid @RequestBody HomeUpdateRequest request, @CurrentUser Long userId) {
        HomeResponse response = homeService.updateHome(homeId, userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}/storage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HomeResponse> updateHomeStorage(@PathVariable Long homeId, @Valid @RequestBody HomeUpdateStorage request, @CurrentUser Long userId) {
        HomeResponse response = homeService.updateStorage(homeId, userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{homeId}")
    public ResponseEntity<Void> deleteHome(@PathVariable Long homeId, @CurrentUser Long userId) {
        homeService.deleteHome(homeId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{homeId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser Long userId) {
        homeService.addMember(homeId, userId, request.username());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{homeId}/members")
    public ResponseEntity<Void> kickMember(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser Long userId) {
        homeService.kickMember(homeId, userId, request.username());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}/members")
    public ResponseEntity<Page<UserDetailsResponse>> viewHomeMembers(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserDetailsResponse> response = homeService.getMembers(homeId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}/owner")
    public ResponseEntity<Void> changeOwner(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser Long userId) {
        homeService.changeOwner(homeId, userId, request.username());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}