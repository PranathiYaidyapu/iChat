package com.ichat.ichat.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ichat.ichat.util.StringSetConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Please enter a valid email")
    @NotBlank(message = "Username (email) is required")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "first name is required")
    @Size(min = 2, message = "first name must be at least 2 characters")
    @Column(name="first_name")
    private String firstname;

    @NotBlank(message = "last name is required")
    @Size(min = 2, message = "last name must be at least 2 characters")
     @Column(name="last_name")
     private String lastName;
    private boolean enabled = true;

    @Column(name = "role")
    @Convert(converter = StringSetConverter.class)
    private Set<String> roles;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(@Email(message = "Please enter a valid email") @NotBlank(message = "Username (email) is required") String username) {
        this.username = username;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFirstname(@NotBlank(message = "first name is required") @Size(min = 2, message = "first name must be at least 2 characters") String firstname) {
        this.firstname = firstname;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public @NotBlank(message = "first name is required") @Size(min = 2, message = "first name must be at least 2 characters") String getFirstname() {
        return firstname;
    }

    public void setFirstName(@NotBlank(message = "first name is required") @Size(min = 2, message = "first name must be at least 2 characters") String firstname) {
        this.firstname = firstname;
    }

    public @NotBlank(message = "last name is required") @Size(min = 2, message = "last name must be at least 2 characters") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "last name is required") @Size(min = 2, message = "last name must be at least 2 characters") String lastName) {
        this.lastName = lastName;
    }
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Doctor doctor;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
    public String getRoleNames() {
        if (roles == null || roles.isEmpty()) return "";
        return String.join(", ", roles);
    }


    public boolean hasRole(String roleName) {
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(roleName));
    }
    public String getRole() {
        if (roles == null || roles.isEmpty()) return "";

        return roles.iterator().next();
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
