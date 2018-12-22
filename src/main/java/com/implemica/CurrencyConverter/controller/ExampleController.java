package com.implemica.CurrencyConverter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Class for education.
 *
 */
@Controller
@Deprecated
public class ExampleController {

   /**
    * Just hello in body.
    * @return body
    */
   @GetMapping(path = "/test")
   public String func() {
      return "welcome";
   }

   /**
    *
    * @return
    */
   @GetMapping(path = "/test1")
   public String test1() {

      return "redirect";
   }

//   @GetMapping(path = "/json")
//   public ResponseEntity<Transaction> json() {
//      return new ResponseEntity<Transaction>(new Transaction("123", "13", "213", "32"), HttpStatus.OK);
//   }
}
