package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

   @Data
   @AllArgsConstructor
   private class Struct{
      private Date date;
      private int id;
      private String firstName;
      private String lastName;
      private String userName;
      private String request;
      private String response;
   }

   @GetMapping("/log")
   public String greeting(Model model) {
      model.addAttribute("transactions", getTransactions());

      return "log";
   }

   @MessageMapping("/hello")
   @SendTo("/topic/greetings")
   public Transaction response(Transaction transaction) {
      return transaction;
   }

   private List<Struct> getTransactions() {

      ArrayList<Struct> result = new ArrayList<>();
      result.add(new Struct(new Date(), 3, "Имя", "Фамилия", "login", "ALL to UAH 5", "234"));
      result.add(new Struct(new Date(), 4, "Фамилия", "Имя", "nickname", "USD to UAH 5", "9"));
      result.add(new Struct(new Date(), 5, "Дима", "Стол", "booom", "UAH to UAH 5", "9.76"));
      result.add(new Struct(new Date(), 6, "Даша", "Сту", "plitka", "USD to RUB 5", "567"));

      return result;
   }
}
