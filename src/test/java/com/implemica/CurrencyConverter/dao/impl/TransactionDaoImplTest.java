package com.implemica.CurrencyConverter.dao.impl;

import com.implemica.CurrencyConverter.model.Transaction;
import com.implemica.CurrencyConverter.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sun.deploy.util.SystemUtils.deleteRecursive;
import static org.junit.Assert.assertEquals;

class TransactionDaoImplTest {

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";


   private static final String CONVERT_MESSAGE = "You can use /convert to make me new convert currencies";
   /**
    * Stop message to user
    */
   private static final String STOP_MESSAGE = "OK. " + CONVERT_MESSAGE;

   /**
    * Bot's command to start conversation
    */
   private static final String START_COMMAND = "/start";
   /**
    * Bot's command to start convert currencies
    */
   private static final String CONVERT_COMMAND = "/convert";
   /**
    * Bot's command to stop conversation
    */
   private static final String STOP_COMMAND = "/stop";
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   private static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   private static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";


   private static File tempFile;
   private static TransactionDaoImpl tr;
   private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");


   @BeforeAll
   static void beforeClass() {
      tempFile = new File("testData.csv");
      tr = new TransactionDaoImpl(tempFile);
   }


   @Test
   void write() {


   }

   @Test
   void getAll() throws ParseException {
      Transaction tr10 = createTransaction("27.12.2018 04:22:47", 13, "Vasiliy", "Ivanov", "", START_COMMAND, START_MESSAGE);
      Transaction tr11 = createTransaction("27.12.2018 04:23:04", 13, "Vasiliy", "Ivanov", "", CONVERT_COMMAND, FIRST_CONVERT_MESSAGE);
      Transaction tr12 = createTransaction("27.12.2018 04:23:35", 13, "Vasiliy", "Ivanov", "", "usd", SECOND_CONVERT_MESSAGE_1 + "USD" + SECOND_CONVERT_MESSAGE_2);
      Transaction tr13 = createTransaction("27.12.2018 04:23:58", 13, "Vasiliy", "Ivanov", "", "uah", THIRD_CONVERT_MESSAGE + "USD to UAH");
      Transaction tr14 = createTransaction("27.12.2018 04:24:15", 13, "Vasiliy", "Ivanov", "", "10", "274");

      Transaction tr20 = createTransaction("27.12.2018 12:24:47", 67, "Natalia", "Nikitina", "flower", START_COMMAND, START_MESSAGE);
      Transaction tr21 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina", "flower", "hello", "Sorry, but I don't understand what does \"hello\" mean. " + CONVERT_MESSAGE);
      Transaction tr22 = createTransaction("27.12.2018 12:26:56", 67, "Natalia", "Nikitina", "flower", CONVERT_COMMAND, FIRST_CONVERT_MESSAGE);
      Transaction tr23 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina", "flower", "apple", SECOND_CONVERT_MESSAGE_1 + "APPLE" + SECOND_CONVERT_MESSAGE_2);
      Transaction tr24 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina", "flower", "orange", THIRD_CONVERT_MESSAGE + "APPLE to ORANGE");

      writeToFile(tr10);
      writeToFile(tr11);
      writeToFile(tr12);
      writeToFile(tr13);
      writeToFile(tr14);
      writeToFile(tr20);
      writeToFile(tr21);
      writeToFile(tr22);
      writeToFile(tr23);
      writeToFile(tr24);

      List<Transaction> list = tr.getAll();
      assertEquals(tr10, list.get(0));
      assertEquals(tr11, list.get(1));
      assertEquals(tr12, list.get(2));
      assertEquals(tr13, list.get(3));
      assertEquals(tr14, list.get(4));
      assertEquals(tr20, list.get(5));
      assertEquals(tr21, list.get(6));
      assertEquals(tr22, list.get(7));
      assertEquals(tr23, list.get(8));
      assertEquals(tr24, list.get(9));

   }

   @Test
   void getByDate() {
   }


   private void writeToFile(Transaction transaction) {
      tr.write(transaction);
   }

   private Transaction createTransaction(String date, int userId, String userFirstName, String userLastName, String userName, String usersRequest, String botsResponse) throws ParseException {
      Date parseDate = df.parse(date);
      User user = new User(userId, userFirstName, userLastName, userName);
      return new Transaction(parseDate, user, usersRequest, botsResponse);
   }

   @AfterAll
   static void afterClass() throws IOException {
      if (tempFile == null) {
         return;
      }
      deleteRecursive(tempFile);
   }
}