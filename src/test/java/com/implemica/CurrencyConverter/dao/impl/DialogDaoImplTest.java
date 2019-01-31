package com.implemica.CurrencyConverter.dao.impl;

import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests DialogDaoImpl class.
 *
 * @author Daria S.
 */
public class DialogDaoImplTest {

   /**
    * Message to the user with the suggestion of a new conversion
    */
   private static final String CONVERT_MESSAGE = "\nYou can make a new currency conversion: \n\n" +
           "➡️ using /convert command \nor\n➡️ single line command " +
           "(E. g. : 10 USD in UAH)";
   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "\uD83D\uDC4B Hello! I can help you to convert currencies."
           + CONVERT_MESSAGE + "\n\nSo, how can I help you?";
   /**
    * Stop message to the user
    */
   private static final String STOP_MESSAGE = "🆗." + CONVERT_MESSAGE;

   /**
    * Bot's command to start conversation
    */
   private static final String START = "/start";
   /**
    * Bot's command to start convert currencies
    */
   private static final String CONVERT = "/convert";

   /**
    * Bot's command to stop conversation
    */
   private static final String STOP = "/stop";
   /**
    * Bot's response for /convert command
    */
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (E. g. : USD)";
   /**
    * Start of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_1 = "What currency do you want to convert from ";
   /**
    * End of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_2 = " to? (E. g. : EUR)";
   /**
    * Bot's response after entering second currency
    */
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";

   /**
    * Temporary File, which stores written data
    */
   private static File tempFile;
   /**
    * DialogDaoImpl object for writing and reading dialogs
    */
   private static DialogDaoImpl tr;
   /**
    * Date format
    */
   private SimpleDateFormat df = BotService.SIMPLE_DATE_FORMAT;

   /**
    * Creates temporary file and DialogDaoIml object
    */
   @BeforeAll
   static void beforeClass() {
      tempFile = new File("testData.csv");
      tr = new DialogDaoImpl(tempFile);
   }

   /**
    * Tests, that file was created
    */
   @Test
   void fileWasCreated() {
      assertTrue(tempFile.exists());
   }

   /**
    * Tests, that after writing data to file and data, which was read are equal
    *
    * @throws ParseException if date format is not correct
    */
   @Test
   void writeAndReadTest() throws ParseException {
      int size = tr.getAll().size();

      Dialog tr10 = createTransaction("27.12.2018 04:22:47", 13, "Vasiliy", "Ivanov",
              "", START, START_MESSAGE);
      Dialog tr11 = createTransaction("27.12.2018 04:23:04", 13, "Vasiliy", "Ivanov",
              "", CONVERT, FIRST_CONVERT_MESSAGE);
      Dialog tr12 = createTransaction("27.12.2018 04:23:35", 13, "Vasiliy", "Ivanov",
              "", "usd", SECOND_CONVERT_MESSAGE_1 + "USD" + SECOND_CONVERT_MESSAGE_2);
      Dialog tr13 = createTransaction("27.12.2018 04:23:58", 13, "Vasiliy", "Ivanov",
              "", "uah", THIRD_CONVERT_MESSAGE + "USD to UAH");
      Dialog tr14 = createTransaction("27.12.2018 04:24:15", 13, "Vasiliy", "Ivanov",
              "", "10", "274");

      Dialog tr20 = createTransaction("27.12.2018 12:24:47", 67, "Natalia", "Nikitina",
              "flower", START, START_MESSAGE);
      Dialog tr21 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina",
              "flower", "hello", "Sorry, but I don't understand what does \"hello\" mean. " + CONVERT_MESSAGE);
      Dialog tr22 = createTransaction("27.12.2018 12:26:56", 67, "Natalia", "Nikitina",
              "flower", CONVERT, FIRST_CONVERT_MESSAGE);
      Dialog tr23 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina",
              "flower", "apple", SECOND_CONVERT_MESSAGE_1 + "APPLE" + SECOND_CONVERT_MESSAGE_2);
      Dialog tr24 = createTransaction("27.12.2018 12:26:01", 67, "Natalia", "Nikitina",
              "flower", "orange", THIRD_CONVERT_MESSAGE + "APPLE to ORANGE");
      Dialog tr25 = createTransaction("27.12.2018 12:26:34", 67, "Natalia", "Nikitina",
              "flower", STOP, STOP_MESSAGE);

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
      writeToFile(tr25);

      List<Dialog> list = tr.getAll();
      assertEquals(11 + size, list.size());

      assertTrue(areDialogsEqual(tr10, list.get(size)));
      assertTrue(areDialogsEqual(tr11, list.get(1 + size)));
      assertTrue(areDialogsEqual(tr12, list.get(2 + size)));
      assertTrue(areDialogsEqual(tr13, list.get(3 + size)));
      assertTrue(areDialogsEqual(tr14, list.get(4 + size)));
      assertTrue(areDialogsEqual(tr20, list.get(5 + size)));
      assertTrue(areDialogsEqual(tr21, list.get(6 + size)));
      assertTrue(areDialogsEqual(tr22, list.get(7 + size)));
      assertTrue(areDialogsEqual(tr23, list.get(8 + size)));
      assertTrue(areDialogsEqual(tr24, list.get(9 + size)));
      assertTrue(areDialogsEqual(tr25, list.get(10 + size)));

   }

   /**
    * Tests, that after writing data to file and data, which was read by date are equal to expected
    *
    * @throws ParseException if date format is not correct
    */
   @Test
   void getByDate() throws ParseException {
      Date date = df.parse("25.12.2018 00:00:00");
      int size = tr.getByDate(date).size();

      Dialog tr10 = createTransaction("25.12.2018 02:22:47", 547, "Fedor", "Makeeev",
              "", START, START_MESSAGE);
      Dialog tr11 = createTransaction("25.12.2018 02:23:04", 547, "Fedor", "Makeeev",
              "", CONVERT, FIRST_CONVERT_MESSAGE);
      Dialog tr12 = createTransaction("25.12.2018 02:23:35", 547, "Fedor", "Makeeev",
              "", "usd", SECOND_CONVERT_MESSAGE_1 + "USD" + SECOND_CONVERT_MESSAGE_2);
      Dialog tr13 = createTransaction("25.12.2018 02:23:58", 547, "Fedor", "Makeeev",
              "", "uah", THIRD_CONVERT_MESSAGE + "USD to UAH");
      Dialog tr14 = createTransaction("27.12.2018 02:24:15", 547, "Fedor", "Makeeev",
              "", "10", "274");

      Dialog tr20 = createTransaction("25.12.2018 12:24:47", 8888, "Alice", "Alice",
              "fairy", START, START_MESSAGE);
      Dialog tr21 = createTransaction("25.12.2018 04:08:13", 1345, "JD", "",
              "jd", "hello", "Sorry, but I don't understand what does \"hello\" mean. " + CONVERT_MESSAGE);
      Dialog tr22 = createTransaction("27.12.2018 05:26:56", 1345, "JD", "",
              "jd", CONVERT, FIRST_CONVERT_MESSAGE);
      Dialog tr23 = createTransaction("27.12.2018 05:26:01", 1345, "JD", "",
              "jd", "apple", SECOND_CONVERT_MESSAGE_1 + "APPLE" + SECOND_CONVERT_MESSAGE_2);
      Dialog tr24 = createTransaction("27.12.2018 05:26:01", 1345, "JD", "",
              "jd", "orange", THIRD_CONVERT_MESSAGE + "APPLE to ORANGE");

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

      List<Dialog> list = tr.getByDate(date);
      assertEquals(size + 6, list.size());
      assertTrue(areDialogsEqual(tr10, list.get(size)));
      assertTrue(areDialogsEqual(tr11, list.get(1 + size)));
      assertTrue(areDialogsEqual(tr12, list.get(2 + size)));
      assertTrue(areDialogsEqual(tr13, list.get(3 + size)));
      assertTrue(areDialogsEqual(tr20, list.get(4 + size)));
      assertTrue(areDialogsEqual(tr21, list.get(5 + size)));


   }

   /**
    * Writes data to file
    *
    * @param dialog one line of data, which has to be wrote
    */
   private void writeToFile(Dialog dialog) {
      tr.write(dialog);
   }

   /**
    * Creates dialog, which has to be written
    *
    * @param date          date and time of user's request
    * @param userId        user's id
    * @param userFirstName user's first name
    * @param userLastName  user's last name
    * @param userName      user's username
    * @param usersRequest  user's request to bot
    * @param botsResponse  bot's response to user's request
    * @return dialog, which contains information about last given user's request and bot's response
    * @throws ParseException if date format is incorrect
    */
   private Dialog createTransaction(String date, int userId, String userFirstName, String userLastName, String userName,
                                    String usersRequest, String botsResponse) throws ParseException {
      Date parseDate = df.parse(date);
      User user = new User(userId, userFirstName, userLastName, userName);
      return new Dialog(parseDate, user, usersRequest, botsResponse);
   }

   /**
    * Deletes file and checks it
    */
   @AfterAll
   static void afterClass() {
      if (tempFile == null) {
         return;
      }
      assertTrue(tempFile.delete());
   }

   /**
    * Compares two dialogs.
    *
    * @param dialog1 first dialog
    * @param dialog2 second dialog
    * @return true if the dialogs are equal; false otherwise.
    */
   private boolean areDialogsEqual(Dialog dialog1, Dialog dialog2) {
      return dialog1.getDate().equals(dialog2.getDate()) &&
              areUsersEqual(dialog1.getUser(), dialog2.getUser()) &&
              dialog1.getUsersRequest().equals(dialog2.getUsersRequest()) &&
              dialog1.getBotsResponse().equals(dialog2.getBotsResponse());
   }


   /**
    * Compares two users.
    *
    * @param user1 first dialog
    * @param user2 second dialog
    * @return true if the objects are equal; false otherwise.
    */
   private boolean areUsersEqual(User user1, User user2) {
      return user1.getUserId() == user2.getUserId() &&
              user1.getUserFirstName().equals(user2.getUserFirstName()) &&
              user1.getUserLastName().equals(user2.getUserLastName()) &&
              user1.getUserName().equals(user2.getUserName());

   }
}