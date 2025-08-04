package com.security.bank.service;

import com.security.bank.dto.AdminDto;
import com.security.bank.dto.UserDto;
import com.security.bank.entity.*;
import com.security.bank.jwt.JwtAuthenticationHelper;
import com.security.bank.repository.AccountRepository;
import com.security.bank.repository.RoleRepository;
import com.security.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtAuthenticationHelper authenticationHelper;


    public void registerUser(UserDto userDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(userDto.getPassword());
//        Role role = new Role();
//        role.setRoleName("ROLE_CUSTOMER");

        Role role = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User saveUser = new User();
        saveUser.setName(userDto.getName());
        saveUser.setPassword(encodedPassword);
        saveUser.setUsername(userDto.getUsername());
        saveUser.setIdentityProof(userDto.getIdentityProof());
        saveUser.setNumber(userDto.getNumber());
        saveUser.setRoles(role);
        saveUser.setAddress(userDto.getAddress());
        userRepository.save(saveUser);
    }

    public void registerAdmin(AdminDto adminDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(adminDto.getPassword());
//        Role role = new Role();
//        role.setRoleName("ROLE_ADMIN");

        Role role = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User saveUser = new User();
        saveUser.setName(adminDto.getName());
        saveUser.setPassword(encodedPassword);
        saveUser.setUsername(adminDto.getUsername());
        saveUser.setIdentityProof(adminDto.getIdentityProof());
        saveUser.setNumber(adminDto.getNumber());
        saveUser.setRoles(role);
        saveUser.setAddress(adminDto.getAddress());
        userRepository.save(saveUser);
    }

    public List<User> getAllUsers(String username) {
        List<User> userList = userRepository.findAll();

        return userList.stream().filter(user -> {
                    Role role = user.getRoles(); //Single roles
                    boolean isAdmin = role != null && "ROLE_ADMIN".equals(role.getRoleName());
                    return !isAdmin || user.getUsername().equals(username);
                })
                .collect(Collectors.toList());
    }

    public User getUserByName(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new RuntimeException("User Not Found" + username));

        String currentUserName = authenticationHelper.getCurrentUserName();
        boolean isUserName = user.getRoles() != null && "ROLE_ADMIN"
                .equals(user.getRoles().getRoleName());

        if(isUserName && !user.getUsername().equals(currentUserName)){
            throw new AccessDeniedException("You are not allowed to access another admin's data");
        }

        return user;

//        return userRepository.findByUsername(username).get();
    }

    public String deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "Deleted Successfully";
        }
        return "Error in deletion";
    }

    public String deactivateUser(Long userId, Long accountId) {
        if (userRepository.existsById(userId) && accountRepository.existsById(accountId)) {
            User user = userRepository.findById(userId).get();
            Account account = accountRepository.findById(accountId).get();
            if (user.getAccountList().contains(account)) {
                System.out.println("Account Found");
                account.setStatus("INACTIVE");
                accountRepository.save(account);
            }
            return "Deactivated Account for User with id: " + userId;
        }
        return "ERROR";
    }

    public String activateAccount(Long userId, Long accountId) {
        if (userRepository.existsById(userId) && accountRepository.existsById(accountId)) {
            User user = userRepository.findById(userId).get();
            Account account = accountRepository.findById(accountId).get();
            if (user.getAccountList().contains(account) && account.getStatus().equals("INACTIVE")) {
                System.out.println("1 Account Found");
                account.setStatus("ACTIVE");
                accountRepository.save(account);
                return "Activated Account for User with id: " + userId;
            }
        }
        return "ERROR";
    }

    public List<Account> getAllActiveAccountList() {
        return accountRepository.findAllActiveAccounts();
    }

    public List<Account> getAllInActiveAccountList() {
        return accountRepository.findAllInActiveAccounts();
    }


    public List<Account> byAccType(AccountType accType) {
        return accountRepository.findAllByAccountType(accType);
    }

    public List<Account> byBranchType(BranchType branchType) {
        return accountRepository.findAllByBranch(branchType);
    }

    public User getUserById(Long id, Principal principal) {
        String username = principal.getName();

        User loggedInUser = userRepository.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found with username" + username));

        User requestedUser = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Requested user Not Found with id: " + id));

        boolean isAdmin = loggedInUser.getRoles().getRoleName().equals("ROLE_ADMIN"); //bcz now I'm accepting single role for each user

        if (isAdmin) {
            boolean isAnotherAdmin = requestedUser.getRoles().getRoleName().equals("ROLE_ADMIN");
            if (isAnotherAdmin && !requestedUser.getUsername().equals(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin can't see another admin data");
            }
            return requestedUser;
        }

        if (!requestedUser.getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return requestedUser;
    }
}
