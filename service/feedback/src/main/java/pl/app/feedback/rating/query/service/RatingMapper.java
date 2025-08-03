package pl.app.feedback.rating.query.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.query.dto.RatingDto;
import pl.app.feedback.rating.query.model.UserRating;

@Component
@RequiredArgsConstructor
public class RatingMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(Rating.class, RatingDto.class, e -> modelMapper.map(e, RatingDto.class));
        addMapper(UserRating.Rating.class, RatingDto.class, e -> modelMapper.map(e, RatingDto.class));
    }
}
