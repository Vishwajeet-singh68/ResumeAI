package com.app.mapper;

import com.app.dto.ResumeDTO;
import com.app.entity.Resume;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    ResumeDTO toDTO(Resume resume);

    Resume toEntity(ResumeDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResumeFromDto(ResumeDTO dto, @MappingTarget Resume entity);
}