package com.social.eshop.service.mapper;

import com.social.eshop.domain.*;
import com.social.eshop.service.dto.SubCategoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity SubCategory and its DTO SubCategoryDTO.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, })
public interface SubCategoryMapper extends EntityMapper <SubCategoryDTO, SubCategory> {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category", target = "categoryDTO")
    SubCategoryDTO toDto(SubCategory subCategory);

    /*
    * pay attention to it
    * */

    //  @Mapping(source = "categoryDTO", target ="category" )
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "products", ignore = true)
    SubCategory toEntity(SubCategoryDTO subCategoryDTO);


    default SubCategory fromId(Long id) {
        if (id == null) {
            return null;
        }
        SubCategory subCategory = new SubCategory();
        subCategory.setId(id);
        return subCategory;
    }
}
