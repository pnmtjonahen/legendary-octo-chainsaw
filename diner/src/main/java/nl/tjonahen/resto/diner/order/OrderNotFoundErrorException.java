package nl.tjonahen.resto.diner.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderNotFoundErrorException extends RuntimeException {
    
}
