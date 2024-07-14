package com.cranker.cranker.notification.payload.mapper;

import com.cranker.cranker.notification.model.Notification;
import com.cranker.cranker.notification.payload.NotificationDTO;
import com.cranker.cranker.notification.payload.NotificationRequestDTO;
import com.cranker.cranker.notification.payload.NotificationResponseDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "minutesSince", expression = "java(java.time.temporal.ChronoUnit.MINUTES.between(LocalDateTime.now(), notification.getIssued()))")
    @Mapping(target = "hoursSince", expression = "java(java.time.temporal.ChronoUnit.HOURS.between(LocalDateTime.now(), notification.getIssued()))")
    @Mapping(target = "daysSince", expression = "java(java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), notification.getIssued()))")
    @Mapping(target = "weeksSince", expression = "java(java.time.temporal.ChronoUnit.WEEKS.between(LocalDateTime.now(), notification.getIssued()))")
    @Mapping(target = "monthsSince", expression = "java(java.time.temporal.ChronoUnit.MONTHS.between(LocalDateTime.now(), notification.getIssued()))")
    @Mapping(target = "yearsSince", expression = "java(java.time.temporal.ChronoUnit.YEARS.between(LocalDateTime.now(), notification.getIssued()))")
    NotificationDTO entityToDto(Notification notification);
    Notification notificationRequestToEntity(NotificationRequestDTO notificationRequestDTO);

    Notification notificationDtoToEntity(NotificationDTO dto);

    List<NotificationDTO> entityToDto(Iterable<Notification> notifications);

    @Mapping(target = "notifications", source = "notificationDTOs")
    @Mapping(target = "pageNo", expression = "java(page.getNumber())")
    @Mapping(target = "pageSize", expression = "java(page.getSize())")
    @Mapping(target = "totalElements", expression = "java(page.getTotalElements())")
    @Mapping(target = "totalPages", expression = "java(page.getTotalPages())")
    @Mapping(target = "last", expression = "java(page.isLast())")
    NotificationResponseDTO pageToNotificationResponse(Page<Notification> page, List<NotificationDTO> notificationDTOs);
}
