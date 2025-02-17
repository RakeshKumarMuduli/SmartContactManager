package com.contactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.contactmanager.Dao.contactRepository;
import com.contactmanager.Dao.userRepository;
import com.contactmanager.Entity.Contact;
import com.contactmanager.Entity.User;
import com.contactmanager.helpermessage.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private userRepository userrepository;
	
	@Autowired
	private contactRepository contactrepository;
	
	
	
	
	// principal is used to access the current user's information ...............
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String name = principal.getName();
		System.out.println(name);
		
		User user = userrepository.getUserByUserName(name);
		model.addAttribute("user", user);
	}
	
	@GetMapping("/dash")
	public String dashboard() {
		
		return "user/user_dashboard";
	}
	
	@GetMapping("/addcontact")
	public String addContact( HttpSession session,Model model) {
		
		if (session.getAttribute("message") != null) {
            model.addAttribute("messageContent", session.getAttribute("message"));
            session.removeAttribute("message");
        }
		return "user/contact_form";
	}
	
	@PostMapping("/processcontact")
	public String processContact(@ModelAttribute("con") Contact contact,@RequestParam("profileimage") MultipartFile file,Principal principal,HttpSession session) {
		
	    try {
	        String name = principal.getName();
	        User user = userrepository.getUserByUserName(name);
	        
	        if (file.isEmpty()) {
	            System.out.println("File is empty");
	            contact.setImage(null); // or handle appropriately if image is required
	        } else {
	            // Save the image in local memory
	            File uploadDir = new ClassPathResource("static/images").getFile();
	            if (!uploadDir.exists()) {
	            	
	                uploadDir.mkdirs();
	            }

	            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            
	            contact.setImage(file.getOriginalFilename());
	        }

	        contact.setUser(user);
	        user.getContacts().add(contact);
	        User res = userrepository.save(user);
	        
	        
	        //set the succesfull msg
	        session.setAttribute("message", new Message("successfully registered", "success"));
	        System.out.println(res);
	        
	    } catch (Exception e) {
	        System.out.println(e);
	        session.setAttribute("message", new Message("Something went wrong", "error"));
	        return "redirect:/user/addcontact";
	    }
	    return "redirect:/user/addcontact";
	}
	
	@GetMapping("/view-contacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page , Model m,Principal principal) {
		
		String name = principal.getName();
		User user = this.userrepository.getUserByUserName(name);
		
		Pageable pageable = PageRequest.of(page, 3); // here we set page No. and size means No. of contacts show in one page
		Page<Contact> contacts = this.contactrepository.findByUserId(user.getId(),pageable);
		
		m.addAttribute("currentpage", page);
		m.addAttribute("totalpage", contacts.getTotalPages());
		m.addAttribute("contacts", contacts);
		return "user/view_contacts";
	}
	
	
	@GetMapping("/{id}/contact")
	public String singlecontact(@PathVariable("id") Integer id,Model model,Principal principal) {
		
		// applying authorization by user for single contact view
		String name=principal.getName();
		User user = this.userrepository.getUserByUserName(name);
		
		Optional<Contact> con = Optional.of(this.contactrepository.getById(id));
		Contact contact2 = con.get();
		
		//if current user try to access then contact will save by modal
		if(user.getId() == contact2.getUser().getId()) {
				model.addAttribute("contact2", contact2);
		      }
		
		return "user/singlecontact";
	}
	
	@GetMapping("/delete/{cid}")
	public String deleteContact( @PathVariable("cid") Integer id,Model model,HttpSession session ) {
		
		Optional<Contact> contacts = this.contactrepository.findById(id);
		Contact contact = contacts.get();
		//System.out.println(contact.getName());
		
		this.contactrepository.deleteByContactId(contact.getCid());
		
		contact.setUser(null);
		
		this.contactrepository.delete(contact);
		
		
		return "redirect:/user/view-contacts/0";
	}
	
	//open update form
	@PostMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") Integer id,Model model,HttpSession session) {
		
		Optional<Contact> contacts = this.contactrepository.findById(id);
		Contact contact = contacts.get();
		//System.out.println(contact.getName());
		
		model.addAttribute("contact",contact);
		return "user/updateForm";
	}
  
	//update logic
	@PostMapping("/updatecontact")
	public String processUpadate(@ModelAttribute("con") Contact contact,@RequestParam("profileimage") MultipartFile file,Principal principal,HttpSession session) {
		
		System.out.println(contact.getName()+"  --- "+contact.getCid());
		
	    String name = principal.getName();
	    User user = this.userrepository.getUserByUserName(name);
	    
	    	//old contact details
	    	Contact oldcontact = this.contactrepository.getById(contact.getCid());
	    	//System.out.println(oldcontact.getName());
	    	
			try {
	    	if(!file.isEmpty()) {
	    		
	    		// delete from local
	    		File deletefile = new ClassPathResource("static/images").getFile();
	    		File file1=new File(deletefile,oldcontact.getImage());
	    		file1.delete();
	    		
	    		//upate the image in DB And also local
	    		File uploadDir = new ClassPathResource("static/images").getFile();
	    		Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            
	            contact.setImage(file.getOriginalFilename());
	    	}
	    	else {
	    			contact.setImage(oldcontact.getImage());
	    	}
	    	
			}catch(Exception e) {
				e.printStackTrace();	
			}
			
	    	contact.setUser(user);
	    	this.contactrepository.save(contact);
	    	
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	@GetMapping("/profile")
	public String Profileset(Model model) {
		
		
		return "user/myprofile";
	}
	
	@GetMapping("/setting")
	public String setting() {
		return "user/setting";
	}
}

/*
 npx tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --watch
 */