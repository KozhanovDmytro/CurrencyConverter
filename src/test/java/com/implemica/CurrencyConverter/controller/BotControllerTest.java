package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class tests BotController class
 * @author Daria S.
 * @version 11.01.2019 17:48
 */
@ExtendWith(MockitoExtension.class)
public class BotControllerTest {

   /**
    * Bot name in Telegram.
    */
   private static final String BOT_NAME = "CurrencyConverterImplemicaBot";
   /**
    * It uses for getting and sending messages via Bot API
    */
   private static final String BOT_TOKEN = "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";


   @Mock
   private BotService botService;

   @Mock
   private Update update1;
   @Mock
   private Update update2;
   @Mock
   private Update update3;
   @Mock
   private Update update4;
   @Mock
   private Update update5;

   @Mock
   private Message message1;
   @Mock
   private Message message2;
   @Mock
   private Message message3;
   @Mock
   private Message message4;
   @Mock
   private Message message5;

   @Mock
   private org.telegram.telegrambots.meta.api.objects.User botUser1;
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User botUser2;
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User botUser3;
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User botUser4;

   @Mock
   private User user1;
   @Mock
   private User user2;
   @Mock
   private User user3;
   @Mock
   private User user4;

   @InjectMocks
   private static BotController controller;


   private static String message1Text = "/start";
   private static String message2Text = "/stop";
   private static String message3Text = "/convert";
   private static String message4Text = "hello";


   @BeforeEach
   void setUp() {
      controller = new BotController(botService);
      MockitoAnnotations.initMocks(this);

      when(update1.getMessage()).thenReturn(message1);
      when(update2.getMessage()).thenReturn(message2);
      when(update3.getMessage()).thenReturn(message3);
      when(update4.getMessage()).thenReturn(message4);
      when(update5.getMessage()).thenReturn(message5);

      when(message1.hasText()).thenReturn(true);
      when(message2.hasText()).thenReturn(true);
      when(message3.hasText()).thenReturn(true);
      when(message4.hasText()).thenReturn(true);
      when(message5.hasText()).thenReturn(false);

      when(message1.getText()).thenReturn(message1Text);
      when(message2.getText()).thenReturn(message2Text);
      when(message3.getText()).thenReturn(message3Text);
      when(message4.getText()).thenReturn(message4Text);


      when(message1.getFrom()).thenReturn(botUser1);
      when(message2.getFrom()).thenReturn(botUser2);
      when(message3.getFrom()).thenReturn(botUser3);
      when(message4.getFrom()).thenReturn(botUser4);
      when(message5.getFrom()).thenReturn(botUser1);


      when(botUser1.getId()).thenReturn(145);
      when(botUser1.getFirstName()).thenReturn("ludvig");
      when(botUser1.getLastName()).thenReturn("fourteenth");
      when(botUser1.getUserName()).thenReturn("fox");

      when(botUser2.getId()).thenReturn(1289);
      when(botUser2.getFirstName()).thenReturn("julia");
      when(botUser2.getLastName()).thenReturn(null);
      when(botUser2.getUserName()).thenReturn("jD");

      when(botUser3.getId()).thenReturn(11111);
      when(botUser3.getFirstName()).thenReturn("semen");
      when(botUser3.getLastName()).thenReturn("semen");
      when(botUser3.getUserName()).thenReturn(null);

      when(botUser4.getId()).thenReturn(74);
      when(botUser4.getFirstName()).thenReturn("mike");
      when(botUser4.getLastName()).thenReturn(null);
      when(botUser4.getUserName()).thenReturn(null);

      user1 = controller.getInformationAboutUser(message1);
      user2 = controller.getInformationAboutUser(message2);
      user3 = controller.getInformationAboutUser(message3);
      user4 = controller.getInformationAboutUser(message4);

      when(botService.onUpdateReceived(message1Text, user1)).thenReturn("Hello, could I help you?");
      when(botService.onUpdateReceived(message2Text, user2)).thenReturn("OK. You can use /convert to make me new " +
              "convert currencies");
      when(botService.onUpdateReceived(message3Text, user3)).thenReturn("Please, type in the currency to convert from" +
              " (example: USD)");
      when(botService.onUpdateReceived(message4Text, user4)).thenReturn("Sorry, but I don't understand what \"hello\"" +
              " means. You can use /convert to make me new convert currencies");
      when(botService.onUpdateReceived(BotService.UNIQUE, user1)).thenReturn("Sorry, but this message contains " +
              "incorrect content. Please, don't send me messages, which I can't handle. You can use /convert to make me new " +
              "convert currencies");


   }

   @Test
   public void test() {
      assertEquals(BOT_NAME, controller.getBotUsername());
      assertEquals(BOT_TOKEN, controller.getBotToken());
   }

   @Test
   public void onUpdateReceivedTest() {
      //user with all information
      controller.onUpdateReceived(update1);

      verify(update1).getMessage();
      verify(botService).onUpdateReceived(message1Text, user1);


      //user without last name
      controller.onUpdateReceived(update2);

      verify(update2).getMessage();
      verify(botService).onUpdateReceived(message2Text, user2);

      //user without username
      controller.onUpdateReceived(update3);

      verify(update3).getMessage();
      verify(botService).onUpdateReceived(message3Text, user3);

      //user without last name and username
      controller.onUpdateReceived(update4);

      verify(update4).getMessage();
      verify(botService).onUpdateReceived(message4Text, user4);


      //non-text message
      controller.onUpdateReceived(update5);

      verify(update5).getMessage();
      verify(botService).onUpdateReceived(BotService.UNIQUE, user1);
   }

}
