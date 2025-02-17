package com.contactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.Dao.userRepository;
import com.contactmanager.Entity.User;
import com.contactmanager.helpermessage.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class mycontroller {

	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	userRepository userrepo;
	
	
	@GetMapping("/home")
	public String home() {
		
		return "home";
	}
	
	@GetMapping("/signin")
	public String login() {
		
		return "loginpage";
	}
	
	@GetMapping("/register")																																																																
	public String register(Model model,HttpSession session) {
		
		model.addAttribute("user",new User());
		if (session.getAttribute("message") != null) {
            model.addAttribute("messageContent", session.getAttribute("message"));
           // System.out.println(session.getAttribute("message"));
            session.removeAttribute("message");
        }
		return "registerpage";
	}
	
	
	@PostMapping("/do_register")
	public String handleRegister( @Valid @ModelAttribute("user") User user,  BindingResult bindingResult, @RequestParam("profileimage") MultipartFile file, @RequestParam(value = "enable", defaultValue = "false") boolean enable, Model model,HttpSession session) {

	    try {
	       

	        // Handle validation errors
	        if (bindingResult.hasErrors()) {
	            session.setAttribute("message", new Message("Something went wrong", "error"));
	            return "redirect:/register";
	        }
	        
	        // Check if the terms and conditions are accepted
	        if (!enable) {
	            session.setAttribute("message", new Message("You must accept the terms and conditions!", "error"));
	            return "redirect:/register";
	        }
	        
	        // Handle image upload
	        if (!file.isEmpty()) {
	            File uploadDir = new ClassPathResource("static/images").getFile();
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }

	            String fileName = file.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir.getAbsolutePath(), fileName);
	            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	            user.setImage(fileName);
	        } else {
	            user.setImage("rintu.jpg"); // Set default image if none provided
	        }

	        // Set role and encode password
	        user.setRole("ROLE_USER");
	        user.setPassword(bcrypt.encode(user.getPassword()));

	        // Save user to the database
	        User result = userrepo.save(user);

	        // Success message
	        session.setAttribute("message", new Message("Successfully registered!", "success"));
	        return "redirect:/register";

	    } catch (Exception e) {
	        e.printStackTrace();
	        session.setAttribute("message", new Message("Something went wrong. Please try again!", "error"));
	        return "redirect:/register";
	    }
	}

	
	
}

