package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.model.Transaction;
import com.implemica.CurrencyConverter.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.plugin2.message.Conversation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for show bot's log.
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 */
@RestController
public class WebController {

   @GetMapping(path = "/log")
   public ModelAndView showLog() {
      return new ModelAndView("log", HttpStatus.OK);
   }

   @GetMapping(path = "/getLog", consumes = {"application/json"})
   public ResponseEntity<List<Transaction>> getLog() {
      return new ResponseEntity<>(getTransactions(), HttpStatus.OK);
   }

   private List<Transaction> getTransactions() {
//      TODO this
      return new ArrayList<>();
   }
}
