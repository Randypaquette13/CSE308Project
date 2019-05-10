package cse308.server.restControllers;

import cse308.server.dao.User;
import cse308.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class specifies the endpoints and behavior used by an admin user to manage other user accounts.
 */
@SuppressWarnings("Duplicates")
@RestController
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * This method handles requests from an admin to update a user.
     * @return  Success or Failure, depending on if the user was updated successfully or not.
     */
    @RequestMapping(value = "/updateuser", method = RequestMethod.POST)
    public ResponseEntity updateUser(@RequestBody User updatedUserInfo, HttpServletRequest req){
        HttpSession session = req.getSession(false);
        boolean isAdmin = (boolean)session.getAttribute("isAdmin");
        if(session == null || !isAdmin){
            if(session == null){
                System.out.println("null session");
            }
            else{
                System.out.println("isAdmin session is " + session.getAttribute("isAdmin"));
            }
            System.out.println("Not authorized.");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);     //401 Response, not admin
        }
        System.out.println("isAdmin is " + session.getAttribute("isAdmin"));
        User oldUserInfo = userService.findById(updatedUserInfo.getId());
        if(oldUserInfo == null){
            System.out.println("User " + updatedUserInfo + " not found in DB.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);      //400 Response, user didn't exist
        }
        else{
            System.out.println("Updating user from " + oldUserInfo + " to " + updatedUserInfo);
            userService.updateUser(updatedUserInfo, oldUserInfo);
        }
        if(updatedUserInfo.getId() == session.getAttribute("id")){
            System.out.println("User wants to modify own info, update session and cookie");
            session.setAttribute("email", updatedUserInfo.getEmail());
            session.setAttribute("name", updatedUserInfo.getFirstName());
            session.setAttribute("isAdmin", updatedUserInfo.isAdmin());
            Cookie[] cookies = req.getCookies();
            for(Cookie c : cookies){
                if(c.getName().equals("name")) {
                    System.out.println("Updating cookie with name " + c.getValue());
                    c.setValue(updatedUserInfo.getFirstName());
                }
            }
        }
        return new ResponseEntity(HttpStatus.OK);           //200 Response
    }

    /**
     * This method handles requests from an admin to delete a user.
     * @return  Success or Failure, depending on if the user was deleted successfully or not.
     */
    @RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
    public String deleteUser() {
        return "Admin deleting a user goes here.";
    }

    /**
     * This method handles requests from an admin to register a new user.
     * @return  Success or Failure, depending on if the user was registered successfully or not.
     */
    @RequestMapping(value = "/registeruser", method = RequestMethod.POST)
    public String registerUser(){
        return "Admin registering a user goes here.";
        //will likely end up sending a POST request to /register
    }

    @RequestMapping(value = "/adminlogin", method = RequestMethod.POST)
    public ResponseEntity adminLogin(@RequestBody User user, HttpServletRequest req,
                                     HttpServletResponse response){
        if(req.getSession(false) != null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);  //400 Response, already logged in
        }
        if(userService.validateUser(user)){
            User adminUser = userService.getUser(user.getEmail());
            System.out.println(user + " verified.");
            if(!user.isAdmin()){
                System.out.println(user + " was not an admin.");
                return new ResponseEntity(HttpStatus.UNAUTHORIZED); //401 Response
            }
            HttpSession session = req.getSession();
            session.setAttribute("id", adminUser.getId());
            session.setAttribute("email", adminUser.getEmail());
            session.setAttribute("name", adminUser.getFirstName());
            session.setAttribute("isAdmin", adminUser.isAdmin());
            response.addCookie(new Cookie("name", adminUser.getFirstName()));
            return new ResponseEntity(HttpStatus.OK);           //200 Response
        }
        else{
            System.out.println(user + " not verified.");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED); //401 Response
        }

    }

}
