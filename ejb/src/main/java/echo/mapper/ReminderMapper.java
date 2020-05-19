package echo.mapper;

import echo.dto.PersonDTO;
import echo.dto.ReminderDTO;
import echo.dto.TimeSheetDTO;
import echo.entities.Person;
import echo.entities.Reminder;
import echo.entities.TimeSheet;

import java.util.List;
import java.util.stream.Collectors;

public class ReminderMapper extends SimpleMapper<Reminder, ReminderDTO> {
    private PersonMapper personMapper = new PersonMapper();
    private TimeSheetMapper timeSheetMapper = new TimeSheetMapper();

    @Override
    public Reminder toEntity(ReminderDTO dto, Class<Reminder> clazz) {
        Reminder reminder = super.toEntity(dto, clazz);
        reminder.setPerson(personMapper.toEntity(dto.getPerson(), Person.class));
        reminder.setTimeSheet(timeSheetMapper.toEntity(dto.getTimeSheet(), TimeSheet.class));
        return reminder;
    }

    @Override
    public ReminderDTO toDTO(Reminder entity, Class<ReminderDTO> clazz) {
        ReminderDTO reminderDTO = super.toDTO(entity, clazz);
        reminderDTO.setPerson(personMapper.toDTO(entity.getPerson(), PersonDTO.class));
        reminderDTO.setTimeSheet(timeSheetMapper.toDTO(entity.getTimeSheet(), TimeSheetDTO.class));
        return reminderDTO;
    }

    @Override
    public List<Reminder> toEntity(List<ReminderDTO> dtoList, Class<Reminder> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<ReminderDTO> toDTO(List<Reminder> entityList, Class<ReminderDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }
}