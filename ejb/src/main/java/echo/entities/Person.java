package echo.entities;

import echo.models.ReminderMessages;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static echo.util.StringUtils.orEmpty;

@Entity
public class Person extends SimpleEntity {
    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String emailAddress;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_role",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();

    @OneToMany
    private List<Reminder> reminders = new ArrayList<>();

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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public String getFullName() {
        return orEmpty(firstName) + " " + orEmpty(lastName);
    }

    /**
     * Returns a concatenation of all HTML Messages of the {@link Reminder}, the {@link Person} currently has
     *
     * @param lang The language of the {@link Person} in ISO 639 code
     * @return the HTML message for the {@link Reminder}s of the {@link Person}
     */
    public String getCollectedHTMLReminderMessages(String lang) {
        String greeting = String.format(ReminderMessages.greetings.get(lang), this.getFirstName());
        String closing = ReminderMessages.closings.get(lang);

        Optional<String> messages = this.getReminders()
                .stream()
                .map(r -> r.getHTMLMessage(lang))
                .reduce(String::concat);

        if (messages.isPresent()) {
            return Stream.of(greeting, messages.get(), closing).collect(Collectors.joining());
        } else {
            return "";
        }
    }

}
