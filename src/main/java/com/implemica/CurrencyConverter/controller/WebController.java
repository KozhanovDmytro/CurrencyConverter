package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;

/**
 * Controller for show bot's log.
 *
 * @see DialogDao
 *
 * @author Dmytro K.
 * @version 08.01.2019 5:12
 */
@Controller
public final class WebController {

   /** The instance of {@link DialogDao} needed for getting all data from storage. */
   private final DialogDao dialogDao;

   @Autowired
   public WebController(DialogDao dialogDao) {
      this.dialogDao = dialogDao;
   }

   /**
    * Mapping shows all data to web page which got from storage.
    *
    * @param model object which put data to show on page.
    * @return template page.
    */
   @GetMapping("/log")
   public String log(Model model) {
      model.addAttribute("transactions", dialogDao.getAll());
      model.addAttribute("dateFormatter", BotService.SIMPLE_DATE_FORMAT);

      return "log_page";
   }

   /**
    * Mapping gets page with websocket listener
    *
    * @return template page.
    */
   @GetMapping("/monitor")
   public String monitor() {
      return "monitor_page";
   }

   /**
    * Starting page.
    *
    * @return template page.
    */
   @GetMapping("/")
   public String main() {
      return "index";
   }
}
