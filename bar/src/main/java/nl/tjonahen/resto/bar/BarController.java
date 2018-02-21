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
@RequestMapping("/api/bar")
public class BarController {
    
    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }
    
    
    
    @GetMapping
    @JsonView(PublicView.class)
    public List<Drink> getAllDrinks() {
        return barService.getAllDrinks();
    }
    
    @GetMapping("/{ref}")
    public Drink getDrink(@PathVariable String ref) {
        return barService.getAllDrinks().stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Drink.builder().build());
    }
    
}