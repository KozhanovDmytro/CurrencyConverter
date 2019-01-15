package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This class tests BotController class
 *
 * @author Daria S.
 * @version 11.01.2019 17:48
 */
@ExtendWith(MockitoExtension.class)
public class BotControllerTest {

   /**
    * Bot name in Telegram.
    */
   private static final String BOT_NAME = "currConvBot";
   /**
    * It uses for getting and sending messages via Bot API
    */
   private static final String BOT_TOKEN = "760246131:AAHZf7R9NZbVxxiDh4Dtn_76CH5-8LSpEG4";

   /**
    * Start command
    */
   private static final String message1Text = BotService.START;
   /**
    * Stop command
    */
   private static final String message2Text = BotService.STOP;
   /**
    * Convert command
    */
   private static final String message3Text = BotService.CONVERT;
   /**
    * Some non-command word
    */
   private static final String message4Text = "hello";
   /**
    * Bot's response for start command
    */
   private static final String startMessage = BotService.START_MESSAGE;
   /**
    * Bot's response for stop command
    */
   private static final String stopMessage = BotService.STOP_MESSAGE;
   /**
    * Bot's response for convert command
    */
   private static final String firstConvertMessage = BotService.FIRST_CONVERT_MESSAGE;
   /**
    * Bot's response for non-command word
    */
   private static final String wrongMessage = BotService.INCORRECT_REQUEST_MESSAGE;
   /**
    * Bot's response for message, which contains incorrect content
    */
   private static final String wrongContentMessage = BotService.INCORRECT_CONTENT_MESSAGE;

   /**
    * A dummy implementation for BotService class, represents bot's logic
    */
   @Mock
   private BotService botService;

   /**
    * A dummy implementation for Update class, first incoming update from telegram user
    */
   @Mock
   private Update update;


   /**
    * A dummy implementation for Message class, first message from telegram user
    */
   @Mock
   private Message message;


   /**
    * A dummy implementation for Telegram User class
    */
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User botUser;


   /**
    * BotController object, in which mocked object's have to be injected
    */
   @InjectMocks
   private BotController controller;

   /**
    * Creates new BotController, which depends on mocked botService
    */
   @BeforeEach
   void setUp() {
      controller = new BotController(botService);

   }

   /**
    * Tests, that methods returns bots username and token correctly
    */
   @Test
   void test() {
      assertEquals(BOT_NAME, controller.getBotUsername());
      assertEquals(BOT_TOKEN, controller.getBotToken());
   }

   /**
    * Tests, that if user has all information about himself and his message contains specified text, and if method
    * {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()} and
    * {@link BotService#onUpdateReceived(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest1() {
      //update1 from user1
      messageBehavior(message1Text, true);

      //user with all information

      createUser(145, "ludvig", "fourteenth", "fox");

      verifyController(message1Text, startMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his last name and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#onUpdateReceived(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest2() {
      //update2 from user2
      messageBehavior(message2Text, true);

      //user without last name
      createUser(1289, "Laban", null, "fox");

      verifyController(message2Text, stopMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his username name and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#onUpdateReceived(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest3() {
      //update3 from user3

      messageBehavior(message3Text, true);

      //user without username
      createUser(1111, "dad", "Larsson", null);

      verifyController(message3Text, firstConvertMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his last name and username, and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#onUpdateReceived(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest4() {
      //update4 from user4

      messageBehavior(message4Text, true);

      //user without last name and username
      createUser(74, "squirrel", null, null);

      verifyController(message4Text, wrongMessage);
   }

   /**
    * Tests, that if user has all information about himself and his message doesn't contains text, and if method
    * {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()} and
    * {@link BotService#onUpdateReceived(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest5() {

      //update5 from user1
      messageBehavior(null, false);

      createUser(38210, "tutta", "carlson", "chicken");
      verifyController(BotService.UNIQUE, wrongContentMessage);
   }

   /**
    * Defines the return value when update and message methods of the mocked objects are been called
    *
    * @param messageText text of user's message
    * @param hasText     true, if message has correct content (text), false - otherwise
    */
   private void messageBehavior(String messageText, boolean hasText) {
      when(update.getMessage()).thenReturn(message);
      when(message.hasText()).thenReturn(hasText);
      if (hasText) {
         when(message.getText()).thenReturn(messageText);
      }
      when(message.getFrom()).thenReturn(botUser);
   }

   /**
    * Defines the return value when Telegram User methods of the mocked object is been called
    *
    * @param id       users's id
    * @param name     user's name
    * @param lastName user's last name
    * @param userName user's username
    */
   private void createUser(int id, String name, String lastName, String userName) {
      when(botUser.getId()).thenReturn(id);
      when(botUser.getFirstName()).thenReturn(name);
      when(botUser.getLastName()).thenReturn(lastName);
      when(botUser.getUserName()).thenReturn(userName);
   }

   /**
    * Creates User. Defines the return value when onUpdateReceived method of the mocked object botService is been called.
    * Calls {@link BotController#onUpdateReceived(Update)} method and verifies, that methods {@link Update#getMessage()}
    * and {@link BotService#onUpdateReceived(String, User)} were called with specified parameters.
    *
    * @param messageText  message from User
    * @param responseText response to User
    */
   private void verifyController(String messageText, String responseText) {
      User user = controller.getInformationAboutUser(message);
      doReturn(responseText).when(botService).onUpdateReceived(messageText, user);

      controller.onUpdateReceived(update);

      verify(update).getMessage();
      verify(botService).onUpdateReceived(messageText, user);
   }

}
