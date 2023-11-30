package gov.cabinetoffice.gap.eventservice.dto;

import gov.cabinetoffice.gap.eventservice.enums.EventType;
import gov.cabinetoffice.gap.eventservice.enums.ObjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    String userSub;

    String sessionId;

    EventType eventType;

    String objectId;

    ObjectType objectType;

    Instant timestamp;

}
