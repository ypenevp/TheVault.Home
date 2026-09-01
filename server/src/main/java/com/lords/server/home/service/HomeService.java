package com.lords.server.home.service;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.DuplicateResourceException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.entity.HomeMember;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    private final HomeMemberRepository homeMemberRepository;

    public HomeService(HomeRepository homeRepository, UserRepository userRepository,  HomeMemberRepository homeMemberRepository) {
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.homeMemberRepository = homeMemberRepository;
    }

    public Home createHome(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Home home = new Home();
        home.setName(name);
        home.setOwner(owner);
        home.setTotalSizeInBytes(0L);

        return homeRepository.save(home);
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

}
