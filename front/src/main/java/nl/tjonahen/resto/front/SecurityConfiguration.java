package nl.tjonahen.resto.front;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration
//        extends WebSecurityConfigurerAdapter
{

  /*
   * Disabling csrf is done for testing purposes.  Current setup does not have a API gateway.
   */
  //  @Override
  //  protected void configure(HttpSecurity http) throws Exception {
  //    http.csrf().disable();
  //    http.authorizeRequests()
  //        .antMatchers("/api/**")
  //        .permitAll()
  //        .antMatchers("/actuator/**")
  //        .permitAll();
  //  }
}
