package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/")
public class BartenderController {
    
    private final BartenderService bartenderService;

    public BartenderController(BartenderService barService) {
        this.bartenderService = barService;
    }
    
    
    /**
     * Get all the drinks the bartender can make.
     * @return a list of drinks.
     */
    @GetMapping("/menu")
    @JsonView(PublicView.class)
    public List<Drink> getAllDrinks() {
        return bartenderService.getAllDrinks();
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
