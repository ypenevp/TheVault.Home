package com.lords.server.home.service;

import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.DuplicateResourceException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.dto.request.HomeUpdateRequest;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.entity.Home;
import com.lords.server.home.entity.HomeMember;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    private final HomeMemberRepository homeMemberRepository;

    private final MediaRepository mediaRepository;

    public HomeService(HomeRepository homeRepository, UserRepository userRepository,  HomeMemberRepository homeMemberRepository,  MediaRepository mediaRepository) {
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.homeMemberRepository = homeMemberRepository;
        this.mediaRepository = mediaRepository;
    }

    public HomeResponse createHome(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Home home = new Home();
        home.setName(name);
        home.setOwner(owner);
        home.setTotalSizeInBytes(0L);

        Home saved = homeRepository.save(home);

        return new HomeResponse(
                saved.getId(),
                saved.getName(),
                saved.getOwner().getUsername(),
                saved.getTotalSizeInBytes(),
                saved.getMaxSizeInBytes()
        );
    }

    public HomeResponse getHome(Long homeId, Long currentUserId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home, currentUser)){
            throw new AccessDeniedException("You don't have permission to view this home");
        }

        return new HomeResponse(
                home.getId(),
                home.getName(),
                home.getOwner().getUsername(),
                home.getTotalSizeInBytes(),
                home.getMaxSizeInBytes()
        );
    }

    public HomeResponse updateHome(Long homeId, Long currentUserId, HomeUpdateRequest request) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to edit this home");
        }

        home.setName(request.name());
        Home saved = homeRepository.save(home);
        return new HomeResponse(
                saved.getId(),
                saved.getName(),
                saved.getOwner().getUsername(),
                saved.getTotalSizeInBytes(),
                saved.getMaxSizeInBytes()
        );
    }
    public void addMember(Long homeId, Long currentUserId, String usernameToAdd) {

        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to add members to this home");
        }

        User newMember = userRepository.findByUsername(usernameToAdd)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newMember.getId().equals(home.getOwner().getId())) {
            throw new AccessDeniedException("Owner cannot be added as a member");
        }

        if (homeMemberRepository.existsByHomeAndUser(home, newMember)) {
            throw new DuplicateResourceException("User is already a member of this home");
        }

        HomeMember member = new HomeMember();
        member.setHome(home);
        member.setUser(newMember);

        homeMemberRepository.save(member);
    }

    public void kickMember(Long homeId, Long currentUserId, String usernameToDelete) {

        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to kick members from this home");
        }

        User delMember = userRepository.findByUsername(usernameToDelete)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (delMember.getId().equals(home.getOwner().getId())) {
            throw new AccessDeniedException("Owner cannot be kicked");
        }

        HomeMember delCurrent = homeMemberRepository.findByHomeAndUser(home, delMember).orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        homeMemberRepository.delete(delCurrent);
    }

    public List<UserDetailsResponse> getMembers(Long homeId, Long currentUserId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home, currentUser)){
            throw new AccessDeniedException("You don't have permission to view this home");
        }

        List<HomeMember> members = homeMemberRepository.findAllByHome(home);

        List<UserDetailsResponse> result = members.stream()
                .map(HomeMember::getUser)
                .map(UserDetailsResponse::from)
                .toList();

        return result;
    }

    public List<MediaResponse> getMedia(Long homeId, Long currentUserId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home, currentUser)) {
            throw new AccessDeniedException("You don't have permission to view media in this home");
        }

        List<Media> media = mediaRepository.findAllByHome(home)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        return media.stream()
                .map(MediaResponse::from)
                .toList();
    }

    public void changeOwner(Long homeId, Long currentUserId, String newOwnerUsername) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!home.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the current owner can transfer ownership");
        }

        User newOwner = userRepository.findByUsername(newOwnerUsername).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(homeMemberRepository.existsByHomeAndUser(home, newOwner)) {
            HomeMember newOwnerMember = homeMemberRepository.findByHomeAndUser(home,newOwner).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
            homeMemberRepository.delete(newOwnerMember);
        }
        home.setOwner(newOwner);
        homeRepository.save(home);

        HomeMember member = new HomeMember();
        member.setHome(home);
        member.setUser(currentUser);
        homeMemberRepository.save(member);
    }
}


