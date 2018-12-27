package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.TransactionDao;
import com.implemica.CurrencyConverter.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
   public String greeting(Model model) throws IOException, ParseException {
      model.addAttribute("transactions", transactionDao.getAll());

      return "log";
   }

   @MessageMapping("/hello")
   @SendTo("/topic/greetings")
   public Transaction response(Transaction transaction) {
      return transaction;
   }
}
