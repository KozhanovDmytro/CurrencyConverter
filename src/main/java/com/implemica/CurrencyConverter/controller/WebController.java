package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.DialogDao;
import org.springframework.beans.factory.annotation.Autowired;
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
   private DialogDao dialogDao;

   @GetMapping("/log")
   public String log(Model model) {
      model.addAttribute("transactions", dialogDao.getAll());

      return "log";
   }

   @GetMapping("/monitor")
   public String monitor() {
      return "monitor";
   }
}
