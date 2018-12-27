package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.TransactionDao;
import com.implemica.CurrencyConverter.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for show bot's log.
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 */
@Controller
public class WebController {

   @Autowired
   private TransactionDao transactionDao;

   @GetMapping("/log")
   public String log(Model model) {
      model.addAttribute("transactions", transactionDao.getAll());

      return "log";
   }

   @GetMapping("/test")
   public String testWS(Model model) {
      return "testWS";
   }

   @MessageMapping("/hello")
   @SendTo("/topic/greetings")
   public Transaction response(Transaction transaction) {
      return transaction;
   }
}
