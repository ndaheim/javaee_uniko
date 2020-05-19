package echo.security;

import echo.dao.ContractAccess;
import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.entities.Contract;
import echo.logic.SessionCacheLogic;
import echo.mapper.ContractMapper;
import echo.models.RoleName;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Optional;

@Stateless
public class ContractWebSecurityHandler extends AbstractWebSecurityHandler {

    @EJB
    private ContractAccess contractAccess;
    @EJB
    private SessionCacheLogic sessionCache;

    private ContractMapper contractMapper = new ContractMapper();

    private PersonDTO getCurrentPerson() {
        Optional<PersonDTO> user = sessionCache.getSessionPerson();
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    private ContractDTO getContract(long id) {
        Contract c = contractAccess.findOne(id);
        if (c != null) {
            return contractMapper.toDTO(c, ContractDTO.class);
        }
        throw new IllegalArgumentException("Invalid Contract");
    }

    private boolean isEmployeeInContract(ContractDTO contractDTO) {
        return contractDTO.getEmployee().equals(getCurrentPerson());
    }

    private boolean isEmployeeInContract(TimeSheetDTO timeSheetDTO) {
        long id = timeSheetDTO.getContract().getId();
        return getContract(id).getEmployee().equals(getCurrentPerson());
    }

    private boolean isSupervisorInContract(ContractDTO contractDTO) {
        return contractDTO.getSupervisor().equals(getCurrentPerson());
    }

    private boolean isSupervisorInContract(TimeSheetDTO timeSheetDTO) {
        long id = timeSheetDTO.getContract().getId();
        return getContract(id).getSupervisor().equals(getCurrentPerson());
    }

    private boolean isDelegateInContract(ContractDTO contractDTO) {
        return contractDTO.getDelegates().contains(getCurrentPerson());
    }

    private boolean isDelegateInContract(TimeSheetDTO timeSheetDTO) {
        long id = timeSheetDTO.getContract().getId();
        return getContract(id).getDelegates().contains(getCurrentPerson());
    }

    private boolean isSecretaryInContract(ContractDTO contractDTO) {
        return contractDTO.getSecretaries().contains(getCurrentPerson());
    }

    private boolean isSecretaryInContract(TimeSheetDTO timeSheetDTO) {
        long id = timeSheetDTO.getContract().getId();
        return getContract(id).getSecretaries().contains(getCurrentPerson());
    }

    private boolean hasRoleInContract(ContractDTO contractDTO) {
        return this.isEmployeeInContract(contractDTO)
                || this.isSupervisorInContract(contractDTO)
                || this.isDelegateInContract(contractDTO)
                || this.isSecretaryInContract(contractDTO);
    }

    private boolean hasRoleInContract(TimeSheetDTO timeSheetDTO) {
        return this.isEmployeeInContract(timeSheetDTO)
                || this.isSupervisorInContract(timeSheetDTO)
                || this.isDelegateInContract(timeSheetDTO)
                || this.isSecretaryInContract(timeSheetDTO);
    }

    private boolean canSeeAllContracts(PersonDTO personDTO) {
        return personDTO.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMINISTRATOR);
    }
}
