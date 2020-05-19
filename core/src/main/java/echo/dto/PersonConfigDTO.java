package echo.dto;


import echo.models.PersonConfigIdentifier;

public class PersonConfigDTO extends SimpleDTO {
    private Long personId;
    private PersonConfigIdentifier identifier;
    private String value;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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
