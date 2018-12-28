package com.implemica.CurrencyConverter.dao.impl;

import com.implemica.CurrencyConverter.dao.TransactionDao;
import com.implemica.CurrencyConverter.model.Transaction;
import com.implemica.CurrencyConverter.model.User;
import com.opencsv.CSVWriter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class writes and reads history of users conversations.
 *
 * @author Daria S.
 * @version 26.12.2018 18:24
 */

public class TransactionDaoImpl implements TransactionDao {

   private static final String LINE_END = ";\n";
   private static final char SEPARATOR = ';';
   private static final String ELEMENTS_SEPARATOR = ";";
   /**
    * Stores history
    */
   private File data;
   private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

   private Logger log = Logger.getLogger(TransactionDao.class.getName());


   public TransactionDaoImpl() {
      this(new File("src/main/resources/data.csv"));
   }

   TransactionDaoImpl(File data) {
      this.data = data;
   }


   /**
    * Writes all user's requests and bot's responses to file
    *
    * @param transaction date, information about user, user's request and bot's response
    */
   @Override
   public void write(Transaction transaction) {
      // create FileWriter object with file as parameter
      FileWriter outputFile;
      try {
         outputFile = new FileWriter(data, true);
         // create CSVWriter object fileWriter object as parameter
         CSVWriter writer = new CSVWriter(outputFile, SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, LINE_END);

         // add data to csv
         writer.writeNext(transaction.toCsv());

         // closing writer connection
         writer.flush();
         writer.close();
      } catch (IOException e) {
         log.log(Level.SEVERE, "The changes were not recorded", e);
      }


   }

   /**
    * Returns List of information from .csv file
    */
   @Override
   public List<Transaction> getAll() {
      FileReader fileReader;
      BufferedReader reader;

      String line;
      ArrayList<Transaction> result = new ArrayList<>();
      try {
         fileReader = new FileReader(data);
         reader = new BufferedReader(fileReader);
         while ((line = reader.readLine()) != null) {
            String[] element = line.split(ELEMENTS_SEPARATOR);
            User user = new User(Integer.parseInt(element[1]), element[2], element[3], element[4]);
            Transaction transaction = new Transaction(getDate(element[0]), user, element[5], element[6]);
            result.add(transaction);
         }
         fileReader.close();
         reader.close();
      } catch (IOException ex) {
         result = new ArrayList<>();
      }
      return result;
   }

   /**
    * Returns List of information from .csv file only for given Date
    */

   @Override
   public List<Transaction> getByDate(Date date) {
      ArrayList<Transaction> result = new ArrayList<>();
      ArrayList<Transaction> all = (ArrayList<Transaction>) getAll();
      for(Transaction t: all){
         if(isSameDate(t.getDate(),date)){
            result.add(t);
         }
      }


      return result;
   }

   /**
    * Returns true, if date1 and date2 is same calendar day
    */
   private boolean isSameDate(Date date1, Date date2) {
      int[] firstDate = getDayMonthYear(date1);
      int[] secondDate = getDayMonthYear(date2);
      for (int i = 0; i < firstDate.length; i++) {
         if (firstDate[i] != secondDate[i]) {
            return false;
         }
      }
      return true;
   }

   /**
    * Returns array {day, month, year} for given date
    */
   private int[] getDayMonthYear(Date date) {
      Calendar calendar = GregorianCalendar.getInstance();
      calendar.setTime(date);
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      int month = calendar.get(Calendar.MONTH);
      int year = calendar.get(Calendar.YEAR);
      return new int[]{day, month, year};
   }

   /**
    * Parse string to date
    */
   private Date getDate(String string) {
      Date date = null;

      try {
         date = df.parse(string);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      return date;
   }
}
