package com.depistage.mapper;

import com.depistage.dto.TypeForm;
import com.depistage.dto.TypeResponse;
import com.depistage.entity.TypeDepistage;
import org.springframework.stereotype.Component;

@Component
public class TypeMapper {

    public TypeResponse toResponse(TypeDepistage t) {
        return new TypeResponse(t.getId(), t.getCode(), t.getLibelle(), t.getDescription());
    }

    public TypeForm toForm(TypeDepistage t) {
        TypeForm f = new TypeForm();
        f.setCode(t.getCode());
        f.setLibelle(t.getLibelle());
        f.setDescription(t.getDescription());
        return f;
    }

    public void apply(TypeForm f, TypeDepistage t) {
        t.setCode(f.getCode().trim());
        t.setLibelle(f.getLibelle().trim());
        t.setDescription(f.getDescription() == null || f.getDescription().isBlank() ? null : f.getDescription().trim());
    }
}
