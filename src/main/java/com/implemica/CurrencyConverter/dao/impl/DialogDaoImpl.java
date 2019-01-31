package com.implemica.CurrencyConverter.dao.impl;

import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class writes and reads history of users conversations.
 *
 * @author Daria S.
 * @version 31.01.2019 16:24
 * @see DateUtils
 * @see FileUtils
 */

public class DialogDaoImpl implements DialogDao {

   /**
    * Symbol between elements in the line
    */
   private static final String SEPARATOR = ";";

   /**
    * Path to .csv file, which stores changes
    */
   private static final String DATA_CSV_FILE = "data.csv";
   /**
    * Stores history of changes
    */
   private File data;
   /**
    * Logger for this class
    */
   private Logger logger = LoggerFactory.getLogger(DialogDao.class.getName());

   /**
    * Date format
    */
   private SimpleDateFormat df = BotService.SIMPLE_DATE_FORMAT;

   /**
    * Creates a new DialogDaoImpl instance for the DATA_CSV_FILE
    */
   public DialogDaoImpl() {
      this(new File(DATA_CSV_FILE));
   }

   /**
    * Creates a new DialogDaoImpl instance for the given file
    *
    * @param data file, where stores data
    */
   DialogDaoImpl(File data) {
      this.data = data;
   }

   /**
    * Writes all user's requests and bot's responses to file
    *
    * @param dialog date, information about user, user's request and bot's response
    */
   @Override
   public void write(Dialog dialog) {
      try {
         FileUtils.writeStringToFile(data, dialog.toLine(), Charset.defaultCharset(), true);

      } catch (IOException e) {
         logger.error("The changes were not recorded", e);
      }
   }

   /**
    * Returns all user's conversations
    *
    * @return List of information from .csv file
    */
   @Override
   public List<Dialog> getAll() {
      List<String> lines;
      List<Dialog> result = new ArrayList<>();

      try {
         lines = FileUtils.readLines(data, Charset.defaultCharset());

         for (String line : lines) {
            Dialog dialog = convertFromLineToDialog(line);
            result.add(dialog);
         }

      } catch (IOException e) {
         logger.error("File does not exists: " + data);
      }
      return result;
   }

   /**
    * Converts a String to a Dialog
    *
    * @param line String, which has to be converted to Dialog.
    * @return new Dialog
    */
   private Dialog convertFromLineToDialog(String line) {
      String[] element = line.split(SEPARATOR);
      User user = new User(Integer.parseInt(element[1]), element[2], element[3], element[4]);
      return new Dialog(getDate(element[0]), user, element[5], element[6]);
   }

   /**
    * Returns user's conversations by given date
    *
    * @param date given date, which for is needed to get information
    * @return List of information from .csv file only for given Date
    */

   @Override
   public List<Dialog> getByDate(Date date) {
      ArrayList<Dialog> all = (ArrayList<Dialog>) getAll();
      ArrayList<Dialog> result = new ArrayList<>();
      for (Dialog t : all) {
         if (isSameDate(t.getDate(), date)) {
            result.add(t);
         }
      }
      return result;
   }

   /**
    * Checks, that both date are same
    *
    * @param date1 first date to comparing
    * @param date2 second date to comparing
    * @return true, if date1 and date2 is same calendar day
    */
   private boolean isSameDate(Date date1, Date date2) {
      return DateUtils.isSameDay(date1, date2);
   }

   /**
    * Parses string to date
    *
    * @param string string, which contains a date
    * @return date, which was taken from string
    */
   private Date getDate(String string) {
      Date date = null;

      try {
         date = df.parse(string);

      } catch (ParseException e) {
         logger.error("Given string can not be parsed as a date: " + string);
      }

      return date;
   }
}
