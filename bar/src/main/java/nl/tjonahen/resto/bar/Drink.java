package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RequiredArgsConstructor
@Getter
@Builder
public class Drink {

    @JsonView(PublicView.class)
    private final String ref;
    @JsonView(PublicView.class)
    private final String name;
    @JsonView(PublicView.class)
    private final String description;
    @JsonView(PublicView.class)
    private final Long price;

    private final Long preparationTime;

}
