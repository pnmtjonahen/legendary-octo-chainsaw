package nl.tjonahen.resto.diner.menu;

import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Philippe Tjon - A - Hen philippe@tjonahen.nl
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MenuControllerTest {

    @Rule
    public WireMockRule rule = new WireMockRule(19888);
    
    @Autowired
    private MockMvc mockMvc;
    
    @Ignore
    @Test
    public void testGetMenu() throws Exception {
        
        WireMock wireMock = new WireMock(19888);
        wireMock.register(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/chef")).willReturn(aResponse().withStatus(200).withBody("")));
        wireMock.register(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/bartender")).willReturn(aResponse().withStatus(200).withBody("")));
                
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/menu").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(""));
    }
    
}
