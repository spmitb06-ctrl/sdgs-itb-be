package com.sdgs.itb.service.unit.impl;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.entity.unit.UnitType;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseOrganizationExportDTO;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.infrastructure.unit.repository.UnitTypeRepository;
import com.sdgs.itb.service.unit.OrganizationImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrganizationImportServiceImpl implements OrganizationImportService {

    private final UnitRepository unitRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final GoalRepository goalRepository;

    private final Map<Integer, Goal> goalByNumberCache = new HashMap<>();
    private final Map<String, UnitType> unitTypeCache = new HashMap<>();

    private static final Pattern FRONT_GOAL_PATTERN = Pattern.compile("(?i)^\\s*goal\\s*(\\d{1,2})");

    private Integer extractGoalNumber(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        Matcher matcher = FRONT_GOAL_PATTERN.matcher(text.trim());
        if (matcher.find()) {
            try {
                int num = Integer.parseInt(matcher.group(1));
                if (num >= 1 && num <= 17) {
                    return num;
                }
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private synchronized void initCaches() {
        if (goalByNumberCache.isEmpty()) {
            List<Goal> allGoals = goalRepository.findAll();
            for (Goal g : allGoals) {
                Integer number = g.getGoalNumber();
                if (number == null || number < 1 || number > 17) {
                    number = extractGoalNumber(g.getTitle());
                }
                if (number != null && number >= 1 && number <= 17) {
                    goalByNumberCache.put(number, g);
                }
            }
        }

        if (unitTypeCache.isEmpty()) {
            List<UnitType> existingTypes = unitTypeRepository.findAll();
            for (UnitType ut : existingTypes) {
                if (ut.getName() != null) {
                    unitTypeCache.put(ut.getName().trim().toLowerCase(), ut);
                }
            }
        }
    }

    private Goal resolveGoal(String rawSdg) {
        Integer num = extractGoalNumber(rawSdg);
        if (num != null) {
            return goalByNumberCache.get(num);
        }
        return null;
    }

    private synchronized UnitType resolveOrCreateUnitType(String rawType) {
        if (rawType == null || rawType.trim().isEmpty()) {
            return null;
        }

        String trimmed = rawType.trim();
        String normalizedKey = trimmed.toLowerCase();

        if (unitTypeCache.containsKey(normalizedKey)) {
            return unitTypeCache.get(normalizedKey);
        }

        Optional<UnitType> fromDb = unitTypeRepository.findByName(trimmed);
        if (fromDb.isPresent()) {
            unitTypeCache.put(normalizedKey, fromDb.get());
            return fromDb.get();
        }

        UnitType created = unitTypeRepository.saveAndFlush(
                UnitType.builder().name(trimmed).build()
        );
        unitTypeCache.put(normalizedKey, created);
        return created;
    }

    @Override
    @Transactional
    public void importOrUpdateOrganization(TypesenseOrganizationExportDTO dto) {
        initCaches();

        String title = dto.getTitle() != null ? dto.getTitle().trim() : "";
        String shortname = dto.getShortname() != null ? dto.getShortname().trim() : "";

        // Strictly format name as: [title] ([shortname])
        String unitName;
        if (!title.isEmpty() && !shortname.isEmpty()) {
            unitName = String.format("%s (%s)", title, shortname);
        } else if (!title.isEmpty()) {
            unitName = title;
        } else {
            unitName = String.format("(%s)", shortname);
        }

        if (unitName.isEmpty()) return;

        Long orgId = null;
        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            try {
                orgId = Long.parseLong(dto.getId().trim());
            } catch (NumberFormatException ignored) {}
        }

        UnitType unitType = resolveOrCreateUnitType(dto.getType());

        // Check by exact formatted name FIRST to prevent duplicate key violations
        Optional<Unit> existingOpt = unitRepository.findByName(unitName);

        if (existingOpt.isEmpty() && orgId != null) {
            existingOpt = unitRepository.findFirstByOrganizationId(orgId);
        }

        if (existingOpt.isEmpty() && !shortname.isEmpty()) {
            existingOpt = unitRepository.findByAbbreviation(shortname);
        }

        Unit unit = existingOpt.orElseGet(Unit::new);

        unit.setName(unitName);
        unit.setAbbreviation(shortname.isEmpty() ? null : shortname);
        unit.setOrganizationId(orgId);
        unit.setUnitType(unitType);
        unit.setWeb(dto.getWeb());
        unit.setSlug(dto.getSlug());
        unit.setCite(dto.getCite());
        unit.setPaper(dto.getPaper());
        unit.setPatent(dto.getPatent());
        unit.setProject(dto.getProject());
        unit.setThesis(dto.getThesis());
        unit.setOutreach(dto.getOutreach());

        // Connect SDGs via UnitGoal
        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().forEach(sdgStr -> {
                Goal goal = resolveGoal(sdgStr);
                if (goal != null) {
                    unit.addGoal(goal);
                }
            });
        }

        unitRepository.save(unit);
    }
}