package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(SignupForm signupForm){
    	User user = new User();
    	Role role = roleRepository.findByName("ROLE_FREE_MEMBER");
    	
    	user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setBirthday(signupForm.getBirthday());
        user.setOccupation(signupForm.getOccupation());        
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false); 
        
        return userRepository.save(user);
    }
    
    public boolean isEmailRegistered(String email)	{
		User user = userRepository.findByEmail(email);

		return user != null;
    }
    
    public boolean isSamePassword(String password, String passwordConfirmation){
    	return password.equals(passwordConfirmation);
    }
    
    @Transactional
    public void enableUser(User user) {
    	//一致するデータが見つかれば、そのデータに紐づいたユーザーのenableカラムの値をtrueにする
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> findUsersByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable) {
        return userRepository.findByNameLikeOrFuriganaLike("%" + nameKeyword + "%", "%" + furiganaKeyword + "%", pageable);
    }
    
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    } 
}
