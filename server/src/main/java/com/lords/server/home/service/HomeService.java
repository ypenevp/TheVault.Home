package com.lords.server.home.service;

import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.DuplicateResourceException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.dto.request.HomeUpdateRequest;
import com.lords.server.home.dto.request.HomeUpdateStorage;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.entity.Home;
import com.lords.server.home.entity.HomeMember;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import com.lords.server.note.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    private final HomeMemberRepository homeMemberRepository;

    private final NoteRepository noteRepository;
    private final MediaRepository mediaRepository;

    public HomeService(HomeRepository homeRepository, UserRepository userRepository, HomeMemberRepository homeMemberRepository, MediaRepository mediaRepository, NoteRepository noteRepository) {
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.homeMemberRepository = homeMemberRepository;
        this.mediaRepository = mediaRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional
    public HomeResponse createHome(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Home home = new Home();
        home.setName(name);
        home.setOwner(owner);
        home.setTotalSizeInBytes(0L);

        Home saved = homeRepository.save(home);

        return HomeResponse.from(saved);
    }


    @Transactional(readOnly = true)
    public HomeResponse getHome(Long homeId, Long currentUserId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)
                && !homeMemberRepository.existsByHomeIdAndUserId(homeId, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to view this home");
        }

        return HomeResponse.from(home);
    }
    @Transactional(readOnly = true)
    public Page<HomeResponse> getHomes(Long currentUserId, Pageable pageable) {
        return homeRepository.findAllHomesByUser(currentUserId, pageable)
                .map(HomeResponse::from);
    }


    @Transactional
    public HomeResponse updateHome(Long homeId, Long currentUserId, HomeUpdateRequest request) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to edit this home");
        }

        home.setName(request.name());
        return HomeResponse.from(home);
    }

    @Transactional
    public HomeResponse updateStorage(Long homeId, Long currentUserId, HomeUpdateStorage request) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        home.setMaxSizeInBytes(request.newStorage());
        return HomeResponse.from(home);
    }

    @Transactional
    public void deleteHome(Long homeId, Long currentUserId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to Delete this home");
        }

        List<Media> mediaList = mediaRepository.findAllByHomeOrderByIdAsc(home);

        for (Media media : mediaList) {
            try {
                Path filePath = Path.of(media.getPath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to delete media file: " + media.getPath(), e);
            }
        }

        noteRepository.deleteAllByHome(home);
        mediaRepository.deleteAllByHome(home);
        homeRepository.delete(home);
    }

    @Transactional
    public void addMember(Long homeId, Long currentUserId, String usernameToAdd) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to add members");
        }

        User newMember = userRepository.findByUsername(usernameToAdd)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newMember.getId().equals(home.getOwner().getId())) {
            throw new AccessDeniedException("Owner cannot be added as a member");
        }

        if (homeMemberRepository.existsByHomeAndUser(home, newMember)) {
            throw new DuplicateResourceException("User is already a member");
        }

        HomeMember member = new HomeMember();
        member.setHome(home);
        member.setUser(newMember);
        homeMemberRepository.save(member);
    }

    @Transactional
    public void kickMember(Long homeId, Long currentUserId, String usernameToDelete) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to kick members");
        }

        User delMember = userRepository.findByUsername(usernameToDelete)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (delMember.getId().equals(home.getOwner().getId())) {
            throw new AccessDeniedException("Owner cannot be kicked");
        }

        HomeMember delCurrent = homeMemberRepository.findByHomeAndUser(home, delMember)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        homeMemberRepository.delete(delCurrent);
    }

    @Transactional(readOnly = true)
    public Page<UserDetailsResponse> getMembers(Long homeId, Long currentUserId, Pageable pageable) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)
                && !homeMemberRepository.existsByHomeIdAndUserId(homeId, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to view this home");
        }

        return homeMemberRepository.findAllByHome(home, pageable)
                .map(HomeMember::getUser)
                .map(UserDetailsResponse::from);
    }

    @Transactional
    public void changeOwner(Long homeId, Long currentUserId, String newOwnerUsername) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the current owner can transfer ownership");
        }

        User newOwner = userRepository.findByUsername(newOwnerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (homeMemberRepository.existsByHomeAndUser(home, newOwner)) {
            HomeMember newOwnerMember = homeMemberRepository.findByHomeAndUser(home, newOwner)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
            homeMemberRepository.delete(newOwnerMember);
        }

        home.setOwner(newOwner);

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        HomeMember member = new HomeMember();
        member.setHome(home);
        member.setUser(currentUser);
        homeMemberRepository.save(member);
    }
}


