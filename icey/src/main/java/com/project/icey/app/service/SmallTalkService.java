package com.project.icey.app.service;

import com.project.icey.app.domain.QuestionType;
import com.project.icey.app.domain.SmallTalk;
import com.project.icey.app.domain.SmallTalkList;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.SmallTalkListRepository;
import com.project.icey.app.repository.SmallTalkRepository;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SmallTalkService {

    private final SmallTalkListRepository listRepository;
    private final SmallTalkRepository smallTalkRepository;
    private final SmallTalkGeneratorService smallTalkGeneratorService;



    // 리스트 생성만 (DB 저장 안 함)
    public SmallTalkListDto previewSmallTalkList(User user, String target, String purpose) {
        SmallTalkResponse resDto = smallTalkGeneratorService.generateSmallTalkWithTitle(target, purpose);

        List<SmallTalkDto> allDtos = resDto.getQuestionTips().stream()
                .map(q -> new SmallTalkDto(null, q.getQuestion(), q.getTip(), null, QuestionType.AI, false)) // ✅ show 포함
                .collect(Collectors.toList());

// 랜덤으로 5개는 show=true, 나머지는 false
        Collections.shuffle(allDtos);
        for (int i = 0; i < allDtos.size(); i++) {
            allDtos.get(i).setShow(i < 5);
        }


        return new SmallTalkListDto(
                null,
                target,
                purpose,
                resDto.getTitle(),
                0,
                allDtos
        );
    }



    // 사용자가 "저장" 눌렀을 때 실제 저장
    public SmallTalkList saveSmallTalkList(SmallTalkListSaveRequest request, User user) {
        SmallTalkList list = new SmallTalkList();
        list.setUser(user);
        list.setTarget(request.getTarget());
        list.setPurpose(request.getPurpose());
        list.setTitle(request.getTitle());
        list.setChange_time(
                request.getChange_time() != null ? request.getChange_time() : 0
        );


        for (SmallTalkDto dto : request.getSmallTalks()) {
            validateQuestionNotNull(dto.getQuestion());

            SmallTalk st = new SmallTalk();
            st.setQuestion(dto.getQuestion());
            st.setTip(dto.getTip());
            st.setAnswer(dto.getAnswer());
            st.setShow(dto.isShow());

            // ✅ questionType 자동 매핑
            if (dto.getQuestionType() != null) {
                st.setQuestionType(dto.getQuestionType());
            } else if (dto.getTip() == null) {
                st.setQuestionType(QuestionType.SELF);  // tip이 없으면 사용자가 추가한 질문으로 판단
            } else {
                st.setQuestionType(QuestionType.AI);    // 기본값 (tip이 있으면 AI 질문으로 간주)
            }

            st.setSmallTalkList(list);
            list.getSmallTalks().add(st);
        }

        return listRepository.save(list);
    }


    public SmallTalkListDto getSmallTalkList(Long listId, User user) {
        SmallTalkList list = listRepository.findById(listId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        if (list.getUser() == null || !list.getUser().getId().equals(user.getId())) {
            throw new CoreApiException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        return convertToDto(list);
    }

    @Transactional
    public void editSmallTalks(Long listId, List<SmallTalkEditRequest.EditItem> edits, User user) {
        SmallTalkList list = listRepository.findById(listId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwner(list.getUser(), user);

        Map<Long, SmallTalk> talkMap = list.getSmallTalks().stream()
                .collect(Collectors.toMap(SmallTalk::getId, Function.identity()));

        for (SmallTalkEditRequest.EditItem item : edits) {
            if (item.getId() == null) {
                // 새로 추가
                SmallTalk newTalk = new SmallTalk();
                newTalk.setQuestion(item.getQuestion());
                newTalk.setAnswer(item.getAnswer());
                newTalk.setTip(null); // 사용자 추가 질문이므로 tip 없음
                newTalk.setQuestionType(QuestionType.SELF);
                newTalk.setShow(item.isShow());
                newTalk.setSmallTalkList(list);
                list.getSmallTalks().add(newTalk);
            } else {
                SmallTalk existing = talkMap.get(item.getId());
                if (existing == null) continue;

                if (existing.getQuestionType() != QuestionType.AI) {
                    // AI가 아닌 경우에만 질문 수정 허용
                    existing.setQuestion(item.getQuestion());
                }

                // 답변은 항상 수정 가능
                existing.setAnswer(item.getAnswer());
                existing.setShow(item.isShow());
            }
        }
        listRepository.save(list);
    }



    @Transactional
    public void saveAnswers(Long listId, List<SmallTalkAnswerListRequest.SmallTalkAnswer> answers, User user) {
        SmallTalkList list = listRepository.findById(listId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        validateOwner(list.getUser(), user);

        Map<Long, SmallTalk> talkMap = list.getSmallTalks().stream()
                .collect(Collectors.toMap(SmallTalk::getId, Function.identity()));

        for (SmallTalkAnswerListRequest.SmallTalkAnswer answer : answers) {
            SmallTalk talk = talkMap.get(answer.getQuestionId());
            if (talk != null) {
                talk.setAnswer(answer.getAnswer());
            }
        }

        listRepository.save(list); // cascade = ALL이므로 SmallTalk도 같이 저장됨
    }

    public void deleteQuestion(Long talkId, User user) {
        SmallTalk talk = smallTalkRepository.findById(talkId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwner(talk.getSmallTalkList().getUser(), user);
        smallTalkRepository.delete(talk);
    }

    public void deleteAll(Long listId, User user) {
        SmallTalkList list = listRepository.findById(listId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwner(list.getUser(), user);
        listRepository.delete(list);
    }

    public void updateListTitle(Long listId, String newTitle, User user) {
        SmallTalkList list = listRepository.findById(listId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwner(list.getUser(), user);
        list.setTitle(newTitle);
        listRepository.save(list);
    }

    private void validateOwner(User owner, User currentUser) {
        if (!owner.getId().equals(currentUser.getId())) {
            throw new CoreApiException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
    }

    private void validateQuestionNotNull(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new CoreApiException(ErrorCode.INVALID_REQUEST);
        }
    }


    public List<SmallTalkListDto> getUserSmallTalkLists(User user) {
        List<SmallTalkList> lists = listRepository.findByUserOrderByCreatedAtDesc(user);
        return lists.stream()
                .map(SmallTalkListDto::fromEntity)
                .collect(Collectors.toList());
    }

    private SmallTalkListDto convertToDto(SmallTalkList entity) {
        SmallTalkListDto dto = new SmallTalkListDto();
        dto.setId(entity.getId());
        dto.setTarget(entity.getTarget());
        dto.setPurpose(entity.getPurpose());
        dto.setTitle(entity.getTitle());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setChange_time(entity.getChange_time() != null ? entity.getChange_time() : 0);
        dto.setSmallTalks(entity.getSmallTalks().stream().map(st -> {
            SmallTalkDto d = new SmallTalkDto();
            d.setId(st.getId());
            d.setQuestion(st.getQuestion());
            d.setTip(st.getTip());
            d.setAnswer(st.getAnswer());
            d.setQuestionType(st.getQuestionType());
            d.setShow(st.isShow());
            return d;
        }).collect(Collectors.toList()));
        return dto;
    }
}
