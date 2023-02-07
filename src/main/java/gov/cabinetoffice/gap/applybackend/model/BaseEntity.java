package gov.cabinetoffice.gap.applybackend.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.MappedSuperclass;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@MappedSuperclass
public class BaseEntity {
}
