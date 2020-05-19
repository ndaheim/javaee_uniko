package echo.entities;

import echo.models.PersonConfigIdentifier;

import javax.persistence.*;

@Entity
public class PersonConfig extends SimpleEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Person person;

    @Column
    @Enumerated(EnumType.STRING)
    private PersonConfigIdentifier identifier;

    @Column(length = 4096)
    private String value;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public PersonConfigIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(PersonConfigIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
