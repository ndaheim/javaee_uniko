package echo.beans;

import echo.dto.PersonDTO;
import echo.dto.RoleDTO;
import echo.logic.PersonConfigLogic;
import echo.logic.PersonManagementLogic;
import echo.models.PersonConfigIdentifier;
import echo.models.RoleName;
import echo.security.LoggedInWebSecurityHandler;
import echo.security.SystemWebSecurityHandler;
import echo.util.DateUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static echo.util.StringUtils.orEmpty;

@Named
@ViewScoped
public class PersonManagementBean implements Serializable {
    @EJB
    private PersonManagementLogic personManagementLogic;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;
    @EJB
    private SystemWebSecurityHandler systemWebSecurityHandler;
    @EJB
    private PersonConfigLogic personConfigLogic;

    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;
    private String selectedRoles;
    private PersonDTO selectedPerson;
    private List<RoleDTO> allRoles = null;
    private PersonDTO toSelect;
    private List<String> uniRoles = new ArrayList<>();

    @PostConstruct
    private void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
    }

    /**
     * Returns a {@link List} of all {@link PersonDTO}
     *
     * @return a {@link List} of all {@link PersonDTO}
     */
    public List<PersonDTO> getAllPersons() {
        return personManagementLogic.getAllPersons();
    }

    /**
     * Returns a {@link List} of all {@link RoleDTO}
     *
     * @return a {@link List} of all {@link RoleDTO}
     */
    public List<RoleDTO> getAllRoles() {
        if (allRoles == null) {
            allRoles = personManagementLogic.getAllRoles();
        }

        return allRoles;
    }

    /**
     * Selects the given {@link PersonDTO}
     *
     * @param selectedPerson
     */
    @SuppressWarnings("unused")
    public void onSelect(PersonDTO selectedPerson) {
        this.selectedPerson = selectedPerson;
        this.uniRoles = personManagementLogic.getUniRoles(selectedPerson.getId());
        firstName = orEmpty(selectedPerson.getFirstName());
        lastName = selectedPerson.getLastName();
        email = selectedPerson.getEmailAddress();
        birthday = selectedPerson.getDateOfBirth() == null ? null : DateUtil.toDate(selectedPerson.getDateOfBirth());
        selectedRoles = selectedPerson.getRoles()
                .stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));
    }

    /**
     * Saves the currently selected {@link PersonDTO}
     */
    @SuppressWarnings("unused")
    public void save() {
        if (selectedPerson != null) {
            selectedPerson.setDateOfBirth(getBirthday() == null ? null : DateUtil.toLocalDate(getBirthday()));
            selectedPerson.setRoles(mapRoleStringToList(getSelectedRoles()));
            selectedPerson = personManagementLogic.save(selectedPerson);
            toSelect = selectedPerson;
        }
    }

    /**
     * Converts the given String containing Roles to a {@link List} of {@link RoleDTO}.
     *
     * @param selectedRoles A comma-separated String containing Rolenames
     * @return              A {@link List} of the {@link RoleDTO} contained in the String
     */
    private List<RoleDTO> mapRoleStringToList(String selectedRoles) {
        return Arrays.stream(selectedRoles.split(","))
                .map(this::getRoleByName)
                .collect(Collectors.toList());
    }

    /**
     * Maps a given String containing a Rolename to a {@link RoleDTO}
     *
     * @param roleName A String containing a Rolename
     * @return         The corresponding {@link RoleDTO}
     */
    private RoleDTO getRoleByName(String roleName) {
        return allRoles.stream().filter(r -> r.getName().name().equals(roleName)).findFirst().orElse(null);
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(String selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public PersonDTO getToSelect() {
        return toSelect;
    }

    public void setToSelect(PersonDTO toSelect) {
        this.toSelect = toSelect;
    }

    /**
     * Returns whether a {@link PersonDTO} is selected or not
     * @return
     */
    public String isNoPersonSelected() {
        return selectedPerson == null ? "true" : "false";
    }

    /**
     * Returns an HTML String containing markup for the uni role badges
     *
     * @return an HTML String containing markup for the uni role badges
     */
    public String getUniRolesBadges() {
        return uniRoles.stream()
                .map(r -> "<span class=\"label label-success\">" + r + "</span>")
                .collect(Collectors.joining("&nbsp;"));
    }

    /**
     * Returns all available roles for the currently selected person
     *
     * @return
     */
    public List<RoleDTO> getAvailableRoles() {
        if (this.selectedPerson != null) {
            String config = personConfigLogic.getConfig(selectedPerson.getId(), PersonConfigIdentifier.UNIVERSITY_ROLES);
            if (config != null) {
                if (config.contains("employee")) {
                    return this.getAllRoles();
                } else {
                    if (config.contains("student")) {
                        return this.getAllRoles()
                                .stream()
                                .filter(r -> r.getName().equals(RoleName.GUEST) || r.getName().equals(RoleName.EMPLOYEE))
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return this.getAllRoles();
    }
}
