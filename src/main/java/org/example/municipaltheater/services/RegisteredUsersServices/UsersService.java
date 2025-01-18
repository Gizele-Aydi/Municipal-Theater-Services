package org.example.municipaltheater.services.RegisteredUsersServices;

import org.example.municipaltheater.interfaces.UsersInterfaces.UsersHandlingInterface;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.repositories.TicketsRepository;
import org.example.municipaltheater.utils.DefinedExceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.slf4j.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsersService implements UserDetailsService, UsersHandlingInterface {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final RegisteredUsersRepository UserRepo;
    private final TicketsRepository TicketRepo;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UsersService(RegisteredUsersRepository UserRepo, TicketsRepository ticketRepo, MongoTemplate mongoTemplate) {
        this.UserRepo = UserRepo;
        TicketRepo = ticketRepo;
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Map<String, Object>> findAllUsersWithFilteredFields(Pageable pageable) {
        logger.info("Fetching all users with pagination: page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<RegisteredUser> usersPage = UserRepo.findAll(pageable);
        return usersPage.map(user -> {
            Map<String, Object> userData = filterUserData(user);
            List<Map<String, Object>> filteredTickets =
                    Optional.ofNullable(user.getBookedTickets()).orElse(Collections.emptyList()).stream()
                            .filter(Objects::nonNull).map(this::filterSingleTicket).collect(Collectors.toList());
            userData.put("bookedTickets", filteredTickets);
            List<Map<String, Object>> filteredHistory =
                    Optional.ofNullable(user.getHistory()).orElse(Collections.emptyList()).stream()
                            .filter(Objects::nonNull).map(this::filterSingleHistoryEntry).collect(Collectors.toList());
            userData.put("history", filteredHistory);
            return userData;
        });
    }

    public RegisteredUser saveUser(@Valid RegisteredUser user) {
        logger.info("Attempting to save user: {}", user.getUsername());
        List<String> missingFields = getMissingFields(user);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        validateUserUniqueness(user);
        return UserRepo.save(user);
    }

    public Map<String, Object> findUserByIdWithFilteredFields(String id) {
        RegisteredUser registeredUser = UserRepo.findById(id).orElseThrow(() -> new ONotFoundException("This user wasn't found. ID: " + id));
        List<Ticket> bookedTickets = mongoTemplate.find(Query.query(Criteria.where("user").is(registeredUser)), Ticket.class);
        List<Ticket> history = mongoTemplate.find(Query.query(Criteria.where("user").is(registeredUser).and("paidStatus").is(true)), Ticket.class);
        registeredUser.setBookedTickets(bookedTickets);
        registeredUser.setHistory(history);
        Map<String, Object> userData = filterUserData(registeredUser);
        List<Map<String, Object>> filteredTickets = filterBookedTickets(bookedTickets);
        List<Map<String, Object>> filteredHistory = filterBookingHistory(history);
        userData.put("bookedTickets", filteredTickets);
        userData.put("history", filteredHistory);
        return userData;
    }

    public Optional<RegisteredUser> findUserProfileById(String userId) {
        RegisteredUser registeredUser = UserRepo.findById(userId)
                .orElseThrow(() -> new ONotFoundException("Your profile wasn't found. ID: " + userId));
        return Optional.of(registeredUser);
    }


    public RegisteredUser updateUser(String id, @Valid RegisteredUser updatedUser) {
        logger.info("Attempting to update user with ID: {}", id);
        List<String> missingFields = getMissingFields(updatedUser);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        Optional<RegisteredUser> existingUserOpt = UserRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new ONotFoundException("This user wasn't found. ID: " + id);
        }
        RegisteredUser user = existingUserOpt.get();
        boolean isUpdated = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            user.setUsername(updatedUser.getUsername());
            isUpdated = true;
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            user.setEmail(updatedUser.getEmail());
            isUpdated = true;
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().equals(user.getPassword())) {
            user.setPassword(updatedUser.getPassword());
            isUpdated = true;
        }
        if (!isUpdated) {
            throw new OServiceException("There were no changes in the fields of the user.");
        }
        return UserRepo.save(user);
    }

    public boolean deleteUserByID(String id) {
        logger.info("Attempting to delete user with ID: {}", id);
        if (!UserRepo.existsById(id)) {
            throw new ONotFoundException("This user wasn't found. ID: " + id);
        }
        UserRepo.deleteById(id);
        return true;
    }

    public Map<String, Object> findBookedTicketsForUser(RegisteredUser user) {
        List<Ticket> bookedTickets = mongoTemplate.find(Query.query(Criteria.where("user").is(user)), Ticket.class);
        List<Map<String, Object>> filteredTickets = filterBookedTickets(bookedTickets);
        double totalAmount = bookedTickets.stream().mapToDouble(Ticket::getPrice).sum();
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("BookedTickets", filteredTickets);
        responseData.put("TotalAmount", totalAmount);
        return responseData;
    }

    public List<Map<String, Object>> findBookingHistoryByUser(RegisteredUser user) {
        List<Ticket> history = mongoTemplate.find(Query.query(Criteria.where("user").is(user).and("paidStatus").is(true)), Ticket.class);
        return filterBookingHistory(history);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        RegisteredUser user = UserRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("This user with the following Username wasn't found: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public Map<String, Object> filterUserData(RegisteredUser registeredUser) {
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("userID", registeredUser.getUserID());
        userData.put("username", registeredUser.getUsername());
        userData.put("email", registeredUser.getEmail());
        userData.put("password", registeredUser.getPassword());
        return userData;
    }

    public List<Map<String, Object>> filterBookedTickets(List<Ticket> bookedTickets) {
        return bookedTickets.stream().map(ticket -> {
            Map<String, Object> ticketData = new LinkedHashMap<>();
            ticketData.put("ticketID", ticket.getTicketID());
            ticketData.put("showName", ticket.getShow().getShowName());
            ticketData.put("showDate", ticket.getShow().getShowDate());
            ticketData.put("showStartTime", ticket.getShow().getShowStartTime());
            ticketData.put("seatType", ticket.getSeat());
            ticketData.put("price", ticket.getPrice());
            return ticketData;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> filterBookingHistory(List<Ticket> history) {
        return history.stream().map(ticket -> {
            Map<String, Object> ticketData = new LinkedHashMap<>();
            ticketData.put("ticketID", ticket.getTicketID());
            ticketData.put("showName", ticket.getShow().getShowName());
            ticketData.put("showDate", ticket.getShow().getShowDate());
            ticketData.put("showStartTime", ticket.getShow().getShowStartTime());
            ticketData.put("seatType", ticket.getSeat());
            ticketData.put("price", ticket.getPrice());
            return ticketData;
        }).collect(Collectors.toList());
    }

    public void removeTicketsForDeletedShow(String showId) {
        List<Ticket> tickets = TicketRepo.findByShowId(showId);
        for (Ticket ticket : tickets) {
            RegisteredUser user = ticket.getUser();
            if (user != null && user.getBookedTickets() != null) {
                user.getBookedTickets().remove(ticket);
                UserRepo.save(user);
            }
        }
    }

    private void validateUserUniqueness(RegisteredUser user) {
        if (UserRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new OAlreadyExistsException("Username is already taken.");
        }
        if (UserRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new OAlreadyExistsException("Email is already registered.");
        }
    }

    private List<String> getMissingFields(RegisteredUser user) {
        return Stream.of(
                user.getUsername() == null || user.getUsername().isEmpty() ? "Username" : null,
                user.getEmail() == null || user.getEmail().isEmpty() ? "Email" : null,
                user.getPassword() == null || user.getPassword().isEmpty() ? "Password" : null
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Map<String, Object> filterSingleTicket(Ticket ticket) {
        Map<String, Object> filteredTicket = new HashMap<>();
        filteredTicket.put("ticketId", ticket.getTicketID());
        filteredTicket.put("showName", ticket.getShow().getShowName());
        filteredTicket.put("price", ticket.getPrice());
        return filteredTicket;
    }

    private Map<String, Object> filterSingleHistoryEntry(Ticket historyEntry) {
        Map<String, Object> filteredHistory = new HashMap<>();
        filteredHistory.put("ticketId", historyEntry.getTicketID());
        filteredHistory.put("showName", historyEntry.getShow().getShowName());
        filteredHistory.put("showDate", historyEntry.getShow().getShowDate());
        return filteredHistory;
    }
}
