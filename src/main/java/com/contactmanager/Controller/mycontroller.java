package com.contactmanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		
		return "login";
	}
	
	@GetMapping("/register")																																																																
	public String register(Model model,HttpSession session) {
		
		model.addAttribute("user",new User());
		if (session.getAttribute("message") != null) {
            model.addAttribute("messageContent", session.getAttribute("message"));
           // System.out.println(session.getAttribute("message"));
            session.removeAttribute("message");
        }
		return "register";
	}
	
	
	@PostMapping("/do_register")
	public String handleregister(@Valid @ModelAttribute("user") User user, BindingResult bindingResult ,@RequestParam(value="enable",defaultValue ="false") boolean enable,Model model,HttpSession session) {
		
		try {
			System.out.println(enable);
			System.out.println(user);
			
			if(!enable) {
				System.out.println("you have not agreed term and condition");
				throw new Exception("you have not agreed the term and condition");
			}
			
			
			if(bindingResult.hasErrors()) {
				System.out.println(bindingResult);
				return "register";
			}
			
			user.setRole("ROLE_USER");
			user.setPassword(bcrypt.encode(user.getPassword()));
			
			model.addAttribute("user", user);
			User result = userrepo.save(user);
		
			session.setAttribute("message", new Message("successfully registered", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("something went wrong","error"));
			return "redirect:/register";
		}
		return"redirect:/register";
	}
	
	
}

