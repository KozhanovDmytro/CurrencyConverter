# ![](https://i.imgur.com/7MjyOo2.jpg) CURRENCY CONVERTER

- The project is a **Java-based** server web application that can convert different currencies between each other. 
- The application uses the **Telegram API** to make communication between users and server more comfortable. 
- The application allows to display the entire **list of user requests** and track it in real time. These features are available for viewing on a simple **admin page**.
- The application supports **145 currencies** and some cryptocurrencies.

## APIs used for conversion
The application uses 5 APIs for currency conversion :
  1. bank-ua.com
  2. floatrates.com
  3. javamoney.org
  4. free.currencyconverterapi.com
  5. currencylayer.com

## Installation

First of all make sure that the `application.properties` file exists in the path `/src/main/resources` and has some properties. 

The bot name and its token must be obtained from [BotFather](https://telegram.me/BotFather). Admin's data required to access the admin page.
 
Create the **.war** file via `Maven install` and deploy it.


## Designed by

[![N|Solid](https://implemica.com/img/logo.png)](https://implemica.com) 

**Free Software, Have Fun!**
