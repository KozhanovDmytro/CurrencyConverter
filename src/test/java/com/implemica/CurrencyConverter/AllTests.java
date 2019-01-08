package com.implemica.CurrencyConverter;

import com.implemica.CurrencyConverter.controller.BotTest;
import com.implemica.CurrencyConverter.controller.WebControllerTest;
import com.implemica.CurrencyConverter.dao.impl.DialogDaoImplTest;
import com.implemica.CurrencyConverter.service.ConverterTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({BotTest.class, DialogDaoImplTest.class,
        ConverterTest.class, WebControllerTest.class})
public class AllTests {
}

