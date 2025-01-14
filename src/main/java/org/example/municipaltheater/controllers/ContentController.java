package org.example.municipaltheater.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {

    @GetMapping("/SignUp")
    public String signup(){
        return "signup";
    }

    @GetMapping("/LogIn")
    public String login(){
        return "login";
    }

    @GetMapping("/Home")
    public String home(){
        return "home";
    }

    @GetMapping("/About")
    public String about(){
        return "about";
    }

    @GetMapping("/Events")
    public String events(){
        return "events";
    }

    @GetMapping("/Shows")
    public String shows(){
        return "shows";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/Profile")
    public String profile(){
        return "profile";
    }

    @GetMapping("/Tickets/{showID}/Book")
    public String ticketBook(){
        return "ticketBook";
    }

    @GetMapping("/Tickets/{showID}/Add")
    public String ticketAdd(){
        return "ticketAdd";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Users")
    public String users(){
        return "users";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Tickets/{showID}/Update")
    public String ticketUpdate(){
        return "ticketUpdate";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Tickets/{showID}/Delete")
    public String ticketDelete(){
        return "ticketDelete";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Events/Add")
    public String eventAdd(){
        return "eventAdd";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Events/Update/{id}")
    public String eventUpdate(){
        return "eventUpdate";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Events/Delete/{id}")
    public String eventDelete(){
        return "eventDelete";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Shows/Add")
    public String showAdd(){
        return "showAdd";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Shows/Update/{id}")
    public String showUpdate(){
        return "showUpdate";
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/Shows/Delete/{id}")
    public String showDelete(){
        return "showDelete";
    }

}

