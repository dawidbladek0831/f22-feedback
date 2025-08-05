package pl.app.feedback.reaction.query.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.query.dto.DomainObjectReactionDto;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.model.UserReaction;

@Component
@RequiredArgsConstructor
public class ReactionMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(Reaction.class, ReactionDto.class, e -> modelMapper.map(e, ReactionDto.class));
        addMapper(UserReaction.Reaction.class, ReactionDto.class, e -> modelMapper.map(e, ReactionDto.class));
        addMapper(DomainObjectReaction.class, DomainObjectReactionDto.class, this::mapToDomainObjectReactionDto);
    }

    private DomainObjectReactionDto mapToDomainObjectReactionDto(DomainObjectReaction source) {
        return DomainObjectReactionDto.builder()
                .id(source.getId())
                .domainObjectType(source.getDomainObjectType())
                .domainObjectId(source.getDomainObjectId())
                .reactions(source.getReactions().entrySet().stream().map(e -> new DomainObjectReactionDto.Reaction(e.getKey(), e.getValue())).toList())
                .build();
    }
}
