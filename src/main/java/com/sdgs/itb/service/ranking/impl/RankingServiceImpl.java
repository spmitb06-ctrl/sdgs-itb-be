package com.sdgs.itb.service.ranking.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.ranking.RankingOverview;
import com.sdgs.itb.entity.ranking.RankingQuestion;
import com.sdgs.itb.entity.ranking.RankingSubmission;
import com.sdgs.itb.entity.ranking.RankingSystem;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.ranking.dto.RankingOverviewDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingQuestionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSubmissionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSystemDTO;
import com.sdgs.itb.infrastructure.ranking.mapper.RankingMapper;
import com.sdgs.itb.infrastructure.ranking.repository.RankingOverviewRepository;
import com.sdgs.itb.infrastructure.ranking.repository.RankingQuestionRepository;
import com.sdgs.itb.infrastructure.ranking.repository.RankingSubmissionRepository;
import com.sdgs.itb.infrastructure.ranking.repository.RankingSystemRepository;
import com.sdgs.itb.service.ranking.RankingService;
import com.sdgs.itb.service.ranking.specification.RankingOverviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingSystemRepository rankingSystemRepository;
    private final RankingQuestionRepository questionRepository;
    private final RankingSubmissionRepository submissionRepository;
    private final RankingOverviewRepository overviewRepository;
    private final GoalRepository goalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RankingSystemDTO> getAllRankingSystems() {
        return rankingSystemRepository.findAllActive().stream()
                .map(RankingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RankingSystemDTO createRankingSystem(RankingSystemDTO dto) {
        RankingSystem system = RankingMapper.toEntity(dto);
        RankingSystem saved = rankingSystemRepository.save(system);
        return RankingMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingOverviewDTO> getOverviews(Long systemId, Long goalId, Integer year, Boolean publishedOnly) {
        Specification<RankingOverview> spec = Specification
                .where(RankingOverviewSpecification.notDeleted())
                .and(RankingOverviewSpecification.hasSystemId(systemId))
                .and(RankingOverviewSpecification.hasGoalId(goalId))
                .and(RankingOverviewSpecification.hasYear(year))
                .and(RankingOverviewSpecification.isPublishedOnly(publishedOnly));

        List<RankingOverview> overviews = overviewRepository.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "year").and(Sort.by(Sort.Direction.ASC, "id"))
        );

        return overviews.stream()
                .sorted(Comparator.comparing(
                        o -> (o.getGoal() != null ? o.getGoal().getGoalNumber() : 0),
                        Comparator.nullsFirst(Integer::compareTo)
                ))
                .map(RankingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RankingOverviewDTO saveOrUpdateOverview(RankingOverviewDTO dto) {
        RankingSystem system = rankingSystemRepository.findById(dto.getRankingSystemId())
                .orElseThrow(() -> new DataNotFoundException("Ranking System not found"));

        Goal goal = null;
        if (dto.getGoalId() != null) {
            goal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new DataNotFoundException("Goal not found"));
        }

        Optional<RankingOverview> existingOpt = Optional.empty();
        if (goal != null) {
            existingOpt = overviewRepository.findBySystemGoalAndYear(
                    system.getId(), goal.getId(), dto.getYear()
            );
        }

        Goal finalGoal = goal;
        RankingOverview overview = existingOpt.orElseGet(() -> RankingOverview.builder()
                .rankingSystem(system)
                .goal(finalGoal)
                .year(dto.getYear())
                .build());

        overview.setGoal(finalGoal);
        overview.setRankPosition(dto.getRankPosition());
        overview.setScore(dto.getScore());
        overview.setContent(dto.getContent());
        overview.setDataStartDate(dto.getDataStartDate());
        overview.setDataEndDate(dto.getDataEndDate());
        if (dto.getIsPublished() != null) {
            overview.setIsPublished(dto.getIsPublished());
        }

        RankingOverview saved = overviewRepository.save(overview);
        return RankingMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RankingOverviewDTO togglePublishOverview(Long overviewId, Boolean publish) {
        RankingOverview overview = overviewRepository.findById(overviewId)
                .orElseThrow(() -> new DataNotFoundException("Ranking overview not found"));

        overview.setIsPublished(publish);
        RankingOverview saved = overviewRepository.save(overview);
        return RankingMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteOverview(Long id) {
        RankingOverview overview = overviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ranking overview not found"));
        overview.setDeletedAt(OffsetDateTime.now());
        overviewRepository.save(overview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingQuestionDTO> getQuestionHierarchy(Long systemId, Long goalId, Integer year) {
        List<RankingQuestion> roots;
        if (goalId != null) {
            roots = questionRepository.findRootQuestionsBySystemAndGoal(systemId, goalId);
        } else {
            roots = questionRepository.findAllRootQuestionsBySystem(systemId);
        }

        List<RankingQuestionDTO> dtos = roots.stream()
                .map(RankingMapper::toDTO)
                .collect(Collectors.toList());

        if (year != null) {
            attachSubmissionsRecursive(dtos, year);
        }

        // Terapkan Natural Sort untuk Level 1, Level 2, Level 3, dan Level 4
        sortQuestionsHierarchyRecursively(dtos);

        return dtos;
    }

    private void sortQuestionsHierarchyRecursively(List<RankingQuestionDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;

        dtos.sort((a, b) -> naturalCompareCodes(a.getCode(), b.getCode()));

        for (RankingQuestionDTO dto : dtos) {
            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                sortQuestionsHierarchyRecursively(dto.getChildren());
            }
        }
    }

    private int naturalCompareCodes(String codeA, String codeB) {
        if (codeA == null && codeB == null) return 0;
        if (codeA == null) return -1;
        if (codeB == null) return 1;

        String[] partsA = codeA.trim().split("\\.");
        String[] partsB = codeB.trim().split("\\.");

        int length = Math.max(partsA.length, partsB.length);
        for (int i = 0; i < length; i++) {
            if (i >= partsA.length) return -1;
            if (i >= partsB.length) return 1;

            String partA = partsA[i];
            String partB = partsB[i];

            boolean isNumA = partA.matches("\\d+");
            boolean isNumB = partB.matches("\\d+");

            if (isNumA && isNumB) {
                int numA = Integer.parseInt(partA);
                int numB = Integer.parseInt(partB);
                if (numA != numB) {
                    return Integer.compare(numA, numB);
                }
            } else {
                int strCompare = partA.compareToIgnoreCase(partB);
                if (strCompare != 0) {
                    return strCompare;
                }
            }
        }
        return 0;
    }

    private void attachSubmissionsRecursive(List<RankingQuestionDTO> questionDTOs, Integer year) {
        if (questionDTOs == null || questionDTOs.isEmpty()) return;

        List<Long> questionIds = new ArrayList<>();
        collectAllQuestionIds(questionDTOs, questionIds);

        if (questionIds.isEmpty()) return;

        List<RankingSubmission> submissions = submissionRepository.findByQuestionIdsAndYear(questionIds, year);
        Map<Long, RankingSubmission> submissionMap = submissions.stream()
                .filter(s -> s.getQuestion() != null)
                .collect(Collectors.toMap(
                        s -> s.getQuestion().getId(),
                        s -> s,
                        (existing, replacement) -> existing
                ));

        populateSubmissions(questionDTOs, submissionMap);
    }

    private void collectAllQuestionIds(List<RankingQuestionDTO> dtos, List<Long> ids) {
        for (RankingQuestionDTO dto : dtos) {
            if (dto.getId() != null) {
                ids.add(dto.getId());
            }
            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                collectAllQuestionIds(dto.getChildren(), ids);
            }
        }
    }

    private void populateSubmissions(List<RankingQuestionDTO> dtos, Map<Long, RankingSubmission> map) {
        for (RankingQuestionDTO dto : dtos) {
            if (dto.getId() != null && map.containsKey(dto.getId())) {
                dto.setSubmission(RankingMapper.toDTO(map.get(dto.getId())));
            }
            if (dto.getChildren() != null) {
                populateSubmissions(dto.getChildren(), map);
            }
        }
    }

    @Override
    @Transactional
    public RankingQuestionDTO createQuestion(RankingQuestionDTO dto) {
        RankingSystem system = rankingSystemRepository.findById(dto.getRankingSystemId())
                .orElseThrow(() -> new DataNotFoundException("Ranking system not found"));

        Goal goal = null;
        if (dto.getGoalId() != null) {
            goal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new DataNotFoundException("Goal not found"));
        }

        RankingQuestion parent = null;
        int calculatedLevel = 1;

        if (dto.getParentId() != null) {
            parent = questionRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new DataNotFoundException("Parent question not found"));
            calculatedLevel = parent.getLevel() + 1;
            if (calculatedLevel > 4) {
                throw new IllegalArgumentException("Pertanyaan maksimal sampai Level 4");
            }
            if (goal == null && parent.getGoal() != null) {
                goal = parent.getGoal();
            }
        }

        if (goal == null) {
            throw new IllegalArgumentException("SDG Goal must be specified");
        }

        String trimmedCode = dto.getCode() != null ? dto.getCode().trim() : "";
        if (trimmedCode.isEmpty()) {
            throw new IllegalArgumentException("Metric code cannot be empty");
        }

        // Pengecekan duplikasi kode pada Sistem dan Goal yang sama
        boolean exists = questionRepository.existsByRankingSystemIdAndGoalIdAndCodeAndDeletedAtIsNull(
                system.getId(), goal.getId(), trimmedCode
        );
        if (exists) {
            throw new IllegalArgumentException("Metric with code '" + trimmedCode + "' already exists for this SDG Goal!");
        }

        RankingQuestion question = RankingQuestion.builder()
                .rankingSystem(system)
                .goal(goal)
                .parent(parent)
                .code(trimmedCode)
                .title(dto.getTitle())
                .content(dto.getContent())
                .level(calculatedLevel)
                .build();

        RankingQuestion saved = questionRepository.save(question);
        return RankingMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RankingQuestionDTO updateQuestion(Long id, RankingQuestionDTO dto) {
        RankingQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Question not found"));

        String trimmedCode = dto.getCode() != null ? dto.getCode().trim() : "";
        if (trimmedCode.isEmpty()) {
            throw new IllegalArgumentException("Metric code cannot be empty");
        }

        Goal targetGoal = question.getGoal();
        if (dto.getGoalId() != null) {
            targetGoal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new DataNotFoundException("Goal not found"));
            question.setGoal(targetGoal);
        }

        // Pengecekan duplikasi kode pada Sistem dan Goal yang sama (selain pertanyaan ini)
        if (question.getRankingSystem() != null && targetGoal != null) {
            boolean exists = questionRepository.existsByRankingSystemIdAndGoalIdAndCodeAndIdNotAndDeletedAtIsNull(
                    question.getRankingSystem().getId(), targetGoal.getId(), trimmedCode, id
            );
            if (exists) {
                throw new IllegalArgumentException("Metric with code '" + trimmedCode + "' already exists for this SDG Goal!");
            }
        }

        question.setCode(trimmedCode);
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());

        RankingQuestion saved = questionRepository.save(question);
        return RankingMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        RankingQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Question not found"));
        question.setDeletedAt(OffsetDateTime.now());
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public RankingSubmissionDTO submitAnswer(RankingSubmissionDTO dto) {
        RankingQuestion question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new DataNotFoundException("Question not found"));

        Optional<RankingSubmission> existingOpt = submissionRepository.findByQuestionIdAndYear(
                question.getId(), dto.getSubmissionYear()
        );

        RankingSubmission submission = existingOpt.orElseGet(() -> RankingSubmission.builder()
                .question(question)
                .submissionYear(dto.getSubmissionYear())
                .build());

        submission.setAnswerText(dto.getAnswerText());
        submission.setSourceDocument(dto.getSourceDocument());

        RankingSubmission saved = submissionRepository.save(submission);
        return RankingMapper.toDTO(saved);
    }
}