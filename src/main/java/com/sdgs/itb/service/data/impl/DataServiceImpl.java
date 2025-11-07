package com.sdgs.itb.service.data.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.data.*;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.data.dto.DataDTO;
import com.sdgs.itb.infrastructure.data.mapper.DataMapper;
import com.sdgs.itb.infrastructure.data.repository.*;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.data.DataService;
import com.sdgs.itb.service.data.specification.DataSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    private final DataRepository repository;
    private final DataUnitRepository dataUnitRepository;
    private final DataGoalRepository dataGoalRepository;
    private final DataImageRepository dataImageRepository;
    private final DataCategoryRepository categoryRepository;
    private final ScholarRepository scholarRepository;
    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public DataDTO createData(DataDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new Data!");
//        }

        Data data = DataMapper.toEntity(dto);

        if (dto.getCategoryId() != null) {
            DataCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            data.setDataCategory(category);
        }

        // Save first to generate ID
        Data savedData = repository.saveAndFlush(data);

        // Units
        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> units = unitRepository.findAllById(dto.getUnitIds());
            if (units.isEmpty()) throw new DataNotFoundException("No valid units found");

            for (Unit unit : units) {  // Regular for-loop
                DataUnit af = DataUnit.builder()
                        .data(savedData)
                        .unit(unit)
                        .build();
                savedData.getDataUnits().add(af);
            }
        }

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
            if (goals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            for (Goal goal : goals) { // Regular for-loop
                DataGoal ag = DataGoal.builder()
                        .data(savedData)
                        .goal(goal)
                        .build();

                savedData.getDataGoals().add(ag);
            }
        }

        // Images
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                DataImage img = DataImage.builder()
                        .data(savedData)
                        .imageUrl(url)
                        .build();
                savedData.getDataImages().add(img);
            }
        }

        savedData = repository.save(savedData);
        return DataMapper.toDTO(savedData);
    }

    @Override
    @Transactional
    public DataDTO updateData(Long id, DataDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can edit Data!");
//        }

        Data existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setThumbnailUrl(dto.getThumbnailUrl());
        existing.setSourceUrl(dto.getSourceUrl());
        existing.setEventDate(dto.getEventDate());

        // Category
        if (dto.getCategoryId() != null) {
            DataCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            existing.setDataCategory(category);
        }

        // Units
        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> newUnits = unitRepository.findAllById(dto.getUnitIds());
            if (newUnits.isEmpty()) throw new DataNotFoundException("No valid units found");

            // Fetch all current active DataUnit relations for this Data
            List<DataUnit> existingDataUnits = dataUnitRepository.findAllByDataId(existing.getId());

            // Hard delete DataUnit records that are not in dto.getUnitIds()
            for (DataUnit oldDataUnit : existingDataUnits) {
                if (!dto.getUnitIds().contains(oldDataUnit.getUnit().getId())) {
                    dataUnitRepository.delete(oldDataUnit);
                }
            }

            // Remove units that already exist (to avoid duplicates)
            newUnits.removeIf(unit ->
                    dataUnitRepository.findByDataIdAndUnitId(existing.getId(), unit.getId()).isPresent()
            );

            // Add new DataUnit entries for remaining units
            for (Unit unit : newUnits) {
                DataUnit newDataUnit = DataUnit.builder()
                        .data(existing)
                        .unit(unit)
                        .build();
                existing.getDataUnits().add(newDataUnit);
            }
        }

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> newGoals = goalRepository.findAllById(dto.getGoalIds());
            if (newGoals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            // Fetch all current active DataGoal relations for this Data
            List<DataGoal> existingDataGoals = dataGoalRepository.findAllByDataId(existing.getId());

            // Hard delete DataGoal records that are not in dto.getGoalIds()
            for (DataGoal oldDataGoal : existingDataGoals) {
                if (!dto.getGoalIds().contains(oldDataGoal.getGoal().getId())) {
                    dataGoalRepository.delete(oldDataGoal);
                }
            }

            // Remove goals that already exist (to avoid duplicates)
            newGoals.removeIf(goal ->
                    dataGoalRepository.findByDataIdAndGoalId(existing.getId(), goal.getId()).isPresent()
            );

            // Add new DataGoal entries for remaining goals
            for (Goal goal : newGoals) {
                DataGoal newDataGoal = DataGoal.builder()
                        .data(existing)
                        .goal(goal)
                        .build();
                existing.getDataGoals().add(newDataGoal);
            }
        }

        // Images
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            // Fetch all currently active DataImage records linked to this Data
            List<DataImage> existingDataImages = dataImageRepository.findAllByDataId(existing.getId());

            // Extract existing image URLs for easy comparison
            Set<String> existingImageUrls = existingDataImages.stream()
                    .map(DataImage::getImageUrl)
                    .collect(Collectors.toSet());

            // Convert the incoming list of image URLs into a modifiable set
            Set<String> newImageUrls = new HashSet<>(dto.getImageUrls());

            // Remove URLs that already exist (they don't need re-adding)
            newImageUrls.removeAll(existingImageUrls);

            // Find old URLs that are NOT in the new list → delete them from DB
            List<DataImage> imagesToDelete = existingDataImages.stream()
                    .filter(di -> !dto.getImageUrls().contains(di.getImageUrl()))
                    .toList();

            if (!imagesToDelete.isEmpty()) {
                dataImageRepository.deleteAll(imagesToDelete); // hard delete
            }

            // Add only truly new images
            for (String imageUrl : newImageUrls) {
                DataImage newDataImage = DataImage.builder()
                        .data(existing)
                        .imageUrl(imageUrl)
                        .build();
                dataImageRepository.save(newDataImage);
                existing.getDataImages().add(newDataImage);
            }
        }


        Data saved = repository.save(existing);
        return DataMapper.toDTO(saved);
    }

    @Override
    public void deleteData(Long id) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can delete Data!");
//        }

        Data data = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        // Soft delete related units
        if (data.getDataUnits() != null && !data.getDataUnits().isEmpty()) {
            data.getDataUnits().forEach(af -> af.setDeletedAt(OffsetDateTime.now()));
        }

        // Soft delete the data itself
        data.setDeletedAt(OffsetDateTime.now());

        repository.save(data);
    }

    @Override
    public void deleteDataImage(Long imageId) {
        DataImage dataImage = dataImageRepository.findById(imageId)
                .orElseThrow(() -> new DataNotFoundException("Image not found"));

        // Hard delete
        dataImageRepository.delete(dataImage);
    }


    @Override
    public DataDTO getData(Long id) {
        return repository.findById(id)
                .filter(data -> data.getDeletedAt() == null)
                .map(DataMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Data not found"));
    }

    @Override
    public Page<DataDTO> getData(
            String title,
            List<Long> goalIds,
            List<Long> categoryIds,
            List<Long> unitIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Data> spec = Specification
                .where(DataSpecification.notDeleted())
                .and(DataSpecification.hasTitle(title))
                .and(DataSpecification.hasGoal(goalIds))
                .and(DataSpecification.hasCategories(categoryIds))
                .and(DataSpecification.hasUnit(unitIds))
                .and(DataSpecification.hasYear(year));

        return repository.findAll(spec, pageable).map(DataMapper::toDTO);
    }

}
