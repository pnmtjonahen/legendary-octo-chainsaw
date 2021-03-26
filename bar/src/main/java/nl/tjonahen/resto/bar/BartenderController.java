package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BartenderController {
    
    private final BartenderService bartenderService;
   
    /**
     * Get all the drinks the bartender can make.
     * @return a list of drinks.
     */
    @GetMapping(path = "/menu", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @JsonView(PublicView.class)
    public Flux<Drink> getAllDrinks() {
        return Flux.fromIterable(bartenderService.getAllDrinks());
    }
    
    /**
     * Ask the bartender what a drink will cost
     * @param ref drink reference
     * @return The requested drink info
     */
    @GetMapping("/drink/{ref}")
    public Drink getDrink(@PathVariable String ref) {
        return bartenderService.getAllDrinks().stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Drink.builder().build());
    }
    
}
