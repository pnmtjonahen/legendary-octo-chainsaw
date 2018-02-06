package nl.tjonahen.resto.bar;

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
@RequestMapping("/api/drink")
public class DrinkController {
    
    private final BarService barService;

    public DrinkController(BarService barService) {
        this.barService = barService;
    }
    
    
    
    @GetMapping
    public List<Drink> getAllDrinks() {
        return barService.getAllDrinks();
    }
    
    @GetMapping("/{ref}")
    public Drink getDrink(@PathVariable String ref) {
        return barService.getAllDrinks().stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Drink.builder().build());
    }
    
}
