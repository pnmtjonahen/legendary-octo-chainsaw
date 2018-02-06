package nl.tjonahen.resto.bar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder        
public class Drink {

    private String ref;
    private String name;
    private String description;
    private Long price;
}
