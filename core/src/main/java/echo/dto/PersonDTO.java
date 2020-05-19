package echo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static echo.util.StringUtils.orEmpty;

public class PersonDTO extends SimpleDTO {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String emailAddress;
    private List<RoleDTO> roles = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    public String getFullName() {
        return orEmpty(getFirstName()) + " " + orEmpty(getLastName());
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", getId(), getFullName());
    }
}
