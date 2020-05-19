package echo.validator;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.logic.ContractManagementLogic;
import echo.logic.LoginLogic;
import echo.models.RoleName;
import echo.util.I18nUtil;

import java.util.ArrayList;
import java.util.List;

import static echo.util.StringUtils.isEmpty;

public class ContractValidator {
    private ContractDTO contract;
    private ContractManagementLogic contractManagementLogic;
    private LoginLogic loginLogic;
    private List<String> errorMessages = new ArrayList<>();

    private ContractValidator(ContractDTO contract, ContractManagementLogic contractManagementLogic, LoginLogic loginLogic) {
        this.contract = contract;
        this.contractManagementLogic = contractManagementLogic;
        this.loginLogic = loginLogic;
    }

    public static ContractValidator of(ContractDTO contract, ContractManagementLogic contractManagementLogic, LoginLogic loginLogic) {
        return new ContractValidator(contract, contractManagementLogic, loginLogic);
    }

    public boolean validate() {
        boolean isOk = true;

        if (isPersonEmpty(contract.getEmployee())) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.noemployee"));
        } else if (!loginLogic.readPersonFromLdap(contract.getEmployee().getEmailAddress()).isPresent()) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.novalidemployee"));
        }

        if (isEmpty(contract.getDescription())) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.nodescription"));
        }

        if (contract.getHoursPerWeek() < 1) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.nohoursperweek"));
        }

        if (contract.getArchiveDuration() < 1) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.noarchiveduration"));
        }

        if (isPersonEmpty(contract.getDelegates())) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.nodelegate"));
        }

        if (isPersonEmpty(contract.getSecretaries())) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.nosecretary"));
        }

        if (isPersonEmpty(contract.getSupervisor())) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.nosupervisor"));
        } else if (!hasPersonRole(contract.getSupervisor(), RoleName.SUPERVISOR)) {
            isOk = false;
            errorMessages.add(I18nUtil.getString("contract.error.novalidsupervisor"));
        }

        return isOk;
    }

    private boolean hasPersonRole(PersonDTO person, RoleName role) {
        return contractManagementLogic.hasPersonRole(person.getEmailAddress(), role);
    }

    private boolean isPersonEmpty(PersonDTO person) {
        return person == null || isEmpty(person.getEmailAddress());
    }

    private boolean isPersonEmpty(List<PersonDTO> personList) {
        return personList == null ||
                personList.isEmpty() ||
                personList.stream().anyMatch(p -> isEmpty(p.getEmailAddress()));
    }

    public String getFormattedErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (String msg : errorMessages) {
            sb.append("<li>").append(msg).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
